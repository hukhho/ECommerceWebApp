package io.spring.shoestore.app.http

import PlaceOrderCommand
import ShippingDetails
import io.spring.shoestore.app.config.AuthenticationUtils
import io.spring.shoestore.core.cart.CartService
import io.spring.shoestore.core.cart.SessionId
import io.spring.shoestore.core.orders.*
import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.payment.PaymentMomo
import io.spring.shoestore.redis.RedisInventoryWarehousingRepository
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.math.BigDecimal
import java.util.*

@Controller
class CheckoutController(
    private val orderProcessingService: OrderProcessingService,
    private val orderQueryService: OrderQueryService,
    private val cartService: CartService,
    private val storeAuthProvider: StoreAuthProvider,
    private val authenticationUtils: AuthenticationUtils,
    private val inventoryRepository: RedisInventoryWarehousingRepository,
    private val request: HttpServletRequest
) {
    val defaultSessionId = SessionId.from("00000000-0000-0000-0000-000000000000")

    @GetMapping("/checkout")
    fun showCheckoutPage(
        authentication: Authentication?,
        model: Model,
        @ModelAttribute("shippingDetails") shippingDetails: ShippingDetails?,
        bindingResult: BindingResult
    ): String {
        val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
        if (userDetails == null) {
            log.warn("User not authenticated. Redirecting to login page.")
            return "login"
        }
        val userID = userDetails.getUserID()
        val sessionID = defaultSessionId

        val cartItems = cartService.getCartItems(sessionID, userID)
        val sortedCartItems = cartItems.sortedBy { it.sku }

        if (cartItems.isEmpty()) {
            model.addAttribute("errorMessage", "Cart is empty. Please continue shopping!")
            return "error"
        }

        val grandTotal: BigDecimal = cartItems
            .map { item -> item?.product?.price?.multiply(BigDecimal(item.quantity)) ?: BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { acc, price -> acc.add(price) }

        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("cartItems", sortedCartItems)
        model.addAttribute("shippingDetails", shippingDetails ?: ShippingDetails("", "", "", "", "", "", ""))

        // If there are errors, add the BindingResult to the model
        // Validate ShippingDetails
        val shippingErrors = shippingDetails?.validate()
        if (shippingErrors != null) {
            for (error in shippingErrors) {
                bindingResult.rejectValue(error.first, "error.shippingDetails", error.second)
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.shippingDetails", bindingResult)
        }

        return "checkout"
    }

    @PostMapping("/process-checkout")
    fun processCheckout(
        authentication: Authentication?,
        @ModelAttribute shippingDetails: ShippingDetails,
        bindingResult: BindingResult,
        paymentMethod: String,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        try {
            val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)

            if (userDetails == null) {
                log.warn("User not authenticated. Redirecting to login page.")
                return "login"
            }

            // Validate ShippingDetails
            val shippingErrors = shippingDetails.validate()

            for (error in shippingErrors) {
                bindingResult.rejectValue(error.first, "error.shippingDetails", error.second)
            }

            if (bindingResult.hasErrors()) {
                log.error("Validation errors: ${bindingResult.allErrors}");

                // Add attributes to redirectAttributes
                redirectAttributes.addFlashAttribute("shippingDetails", shippingDetails);
                redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.shippingDetails",
                    bindingResult
                );

                return "redirect:/checkout";
            }
            val userId = userDetails.getUserID()

            val cartItems = cartService.getCartItems(defaultSessionId, userId)
            val orderId = UUID.randomUUID()
            log.info("gen orderId: $orderId")

            for (item in cartItems) {
                log.info("item ${item.sku} ${item.quantity}")
                val reserved = inventoryRepository.reserveInventory(item.sku, item.quantity)
                log.info("reserved $reserved")

                if (reserved) {
                    inventoryRepository.saveOrderReservation(orderId.toString(), item.sku, item.quantity)
                } else {
                    model.addAttribute("errorMessage", "Failed to reserve inventory for SKU: ${item.sku}")
                    return "error"
                }
            }
            val command = PlaceOrderCommand(orderId, userId, null, shippingDetails, paymentMethod)

            val result = orderProcessingService.placeOrder(command)

            return when (result) {
                is OrderSuccess -> {
                    return "redirect:/checkout-payment" + "?orderId=${result.orderId}";
                }
                is OrderFailure -> {
                    model.addAttribute("orderSuccess", false)
                    model.addAttribute("errorMessage", result.reason)
                    "orderError"
                }
            }
        } catch (e: StockInsufficientException) {
            log.error("Stock insufficient error", e)
            throw e
        } catch (e: Exception) {
            log.error("Error occurred", e)
            return "redirect:/error"
        }
    }

    @GetMapping("/checkout-payment")
    fun processCheckoutPayment(
        authentication: Authentication?,
        redirectAttributes: RedirectAttributes,
        orderId: String,
        model: Model
    ): String {
        try {
            val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)

            if (userDetails == null) {
                log.warn("User not authenticated. Redirecting to login page.")
                return "login"
            }

            val order = orderQueryService.retrieveOrderById(UUID.fromString(orderId))

            if (order == null) {
                log.warn("Order with ID $orderId not found.")
                model.addAttribute("errorMessage", "Order not found.")
                return "error" // or redirect to an appropriate error page
            }

            log.info("Retrieved order: $order")
            model.addAttribute("order", order.order)
            model.addAttribute("payments", order.payments)

            val payment = getPendingPayment(order)

            log.info("get payment $payment")
            if(payment == null) {
                model.addAttribute("errorMessage", "Can't not get pending payment for this order!")
                return "/error"
            }

            val paymentMomo = PaymentMomo()

            val baseUrl = getBaseUrl()

            val callbackUrl = "$baseUrl/query-payment"
            val notifyUrl = "$baseUrl/notify-payment"

            val payUrl = paymentMomo.createPaymentRequest(
                payment.id.toString(),
                orderId,
                payment.totalAmount.toLong(),
                order.order.userID.toString(),
                callbackUrl,
                notifyUrl
            )

            return "redirect:${payUrl}"

        } catch (e: Exception) {
            log.error("Error occurred", e)
            return "redirect:/error"
        }
    }

    @GetMapping("/query-payment")
    fun queryCheckoutPayment(
        authentication: Authentication?,
        redirectAttributes: RedirectAttributes,
        orderId: String,
        requestId: String,
        model: Model
    ): String {
        try {
            val orderId = UUID.fromString(orderId)
            val requestId = UUID.fromString(requestId)

            val order = orderQueryService.retrieveOrderById(orderId)
            if (order == null) {
                log.warn("Order with ID $orderId not found.")
                model.addAttribute("errorMessage", "Order not found.")
                return "error"
            }
            log.info("Retrieved order: $order")

            model.addAttribute("order", order.order)
            model.addAttribute("payments", order.payments)

            val payment = getPendingPayment(order)

            log.info("get payment $payment")
            if(payment == null) {
                model.addAttribute("errorMessage", "Can't not get pending payment for this order!")
                return "/error"
            }

            val paymentMomo = PaymentMomo()
            val message = paymentMomo.queryPaymentRequest(
                orderId.toString(),
                requestId.toString(),
            )

            log.info("message $message")

            if(message.equals("Successful.", ignoreCase = true)) {
                order.order.updateOrderStatus(Order.OrderStatus.PROCESSING)
                val result = orderProcessingService.updatePaymentAndOrderStatus(
                    payment.id,
                    PaymentStatus.COMPLETED,
                    order.order,
                    true
                )
                return when (result) {
                    is OrderSuccess -> {
                        model.addAttribute("orderSuccess", true)
                        model.addAttribute("orderId", result.orderId)
                        model.addAttribute("orderDate", result.orderDate)
                        model.addAttribute("totalAmount", result.totalAmount)
                        "orderConfirmation"
                    }
                    is OrderFailure -> {
                        order.order.updateOrderStatus(Order.OrderStatus.CANCELLED)
                        val result = orderProcessingService.updatePaymentAndOrderStatus(
                            payment.id,
                            PaymentStatus.FAILED,
                            order.order,
                            false
                        )
                        model.addAttribute("errorMessage", "Failed to finalize the order.")

                        return "error"
                    }
                }
            } else {

                model.addAttribute("errorMessage", "Failed payment!")
                return "error"
            }

        } catch (e: Exception) {
            log.error("Error occurred", e)
            return "redirect:/error"
        }
    }

    // Simulate a payment process

    @GetMapping("/test222")
    fun processCheckout2(authentication: Authentication?, model: Model): String {
//        val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
//        if (userDetails == null) {
//            log.warn("User not authenticated. Redirecting to login page.")
//            return "login"
//        }
//        val cartItems = cartService.getCartItems(defaultSessionId, userDetails.getUserID())
//        for (item in cartItems) {
//            val reserved = inventoryRepository.reserveInventory(item.sku, item.quantity)
//            if (!reserved) {
//                model.addAttribute("errorMessage", "Failed to reserve inventory for SKU: ${item.sku}")
//                return "checkoutError"
//            }
//        }
        val reserved = inventoryRepository.reserveInventory("SKU001", 2)

        return "checkoutSuccess" // or "checkoutError"
    }

    fun getPendingPayment(orderWithPayment: OrderWithPayment): Payment? {
        if (orderWithPayment.order.orderStatus != Order.OrderStatus.PENDING) {
            throw Exception("Order is not in pending status!")
        }
        return orderWithPayment.payments.firstOrNull {
                it.orderID == orderWithPayment.order.id
                && it.paymentStatus == PaymentStatus.PENDING }
    }

    fun getBaseUrl(): String {
        val requestURL = StringBuffer(request.requestURL.toString())
        val servletPath = request.servletPath
        return requestURL.substring(0, requestURL.indexOf(servletPath))
    }

    companion object {
        private val log = LoggerFactory.getLogger(CheckoutController::class.java)
    }
}