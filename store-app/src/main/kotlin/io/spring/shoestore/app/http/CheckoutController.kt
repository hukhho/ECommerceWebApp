package io.spring.shoestore.app.http

import PlaceOrderCommand
import ShippingDetails
import io.spring.shoestore.app.config.AuthenticationUtils
import io.spring.shoestore.core.cart.CartService
import io.spring.shoestore.core.cart.SessionId
import io.spring.shoestore.core.orders.*
import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.redis.RedisInventoryWarehousingRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import java.math.BigDecimal

@Controller
class CheckoutController(
    private val orderProcessingService: OrderProcessingService,
    private val orderQueryService: OrderQueryService,
    private val cartService: CartService,
    private val storeAuthProvider: StoreAuthProvider,
    private val authenticationUtils: AuthenticationUtils,
    private val inventoryRepository: RedisInventoryWarehousingRepository // Inject the Redis repository
) {
    val defaultSessionId = SessionId.from("00000000-0000-0000-0000-000000000000")

    @GetMapping("/checkout")
    fun showCheckoutPage(authentication: Authentication?,
                         model: Model,
                         @ModelAttribute("shippingDetails") shippingDetails: ShippingDetails?,
                         bindingResult: BindingResult): String {
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
        model.addAttribute("shippingDetails", shippingDetails ?: ShippingDetails("","","","","","",""))

        // If there are errors, add the BindingResult to the model
        // Validate ShippingDetails
        val shippingErrors = shippingDetails?.validate()
        if (shippingErrors != null) {
            for (error in shippingErrors) {
                bindingResult.rejectValue(error.first,"error.shippingDetails", error.second)
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.shippingDetails", bindingResult)
        }

        return "checkout"
    }

    @GetMapping("/process-checkout")
    fun processCheckout(authentication: Authentication?,

                        model: Model): String {
        val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
        if (userDetails == null) {
            log.warn("User not authenticated. Redirecting to login page.")
            return "login"
        }

        val cartItems = cartService.getCartItems(defaultSessionId, userDetails.getUserID())
        for (item in cartItems) {
            log.info("item ${item.sku} ${item.quantity}")
            val reserved = inventoryRepository.reserveInventory(item.sku, item.quantity)
            if (!reserved) {
                model.addAttribute("errorMessage", "Failed to reserve inventory for SKU: ${item.sku}")
                return "error"
            }
        }

        // Simulate payment processing
        val paymentSuccess = simulatePaymentProcess()
        if (paymentSuccess) {
            log.info("Payment success!!!!!!!!!")

            val orderSuccess = true;

            if (orderSuccess) {
                val userId = userDetails.getUserID()

                val shippingDetails = ShippingDetails("test","test","test","test","test","test", "test")
                val command = PlaceOrderCommand(userId, null, shippingDetails)
                //
                val result = orderProcessingService.placeOrder(command)

                return when (result) {
                    is OrderSuccess -> {
                        model.addAttribute("orderSuccess", true)
                        model.addAttribute("orderId", result.orderId)
                        model.addAttribute("orderDate", result.orderDate)
                        model.addAttribute("totalAmount", result.totalAmount)
                        "orderConfirmation"
                    }
                    is OrderFailure -> {
                        model.addAttribute("orderSuccess", false)
                        model.addAttribute("errorMessage", result.reason)
                        "orderError"
                    }
                }

                return "error"
            } else {
                // If there's an issue finalizing the order, release the reserved inventory
                cartItems.forEach { item ->
                    inventoryRepository.releaseInventory(item.sku, item.quantity)
                }
                model.addAttribute("errorMessage", "Failed to finalize the order.")
                return "error"
            }
        } else {
            // If payment fails, release the reserved inventory
            cartItems.forEach { item ->
                inventoryRepository.releaseInventory(item.sku, item.quantity)
            }
            model.addAttribute("errorMessage", "Payment failed.")
            return "checkoutError"
        }
    }

    // Simulate a payment process
    fun simulatePaymentProcess(): Boolean {
        // Here, you'd typically integrate with a payment gateway or provider.
        // For the sake of this example, we'll simulate a successful payment.
        return true
    }



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



    companion object {
        private val log = LoggerFactory.getLogger(CheckoutController::class.java)
    }
}