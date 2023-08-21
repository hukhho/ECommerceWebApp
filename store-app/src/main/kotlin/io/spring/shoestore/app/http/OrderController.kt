package io.spring.shoestore.app.http

import io.spring.shoestore.app.config.AuthenticationUtils
import io.spring.shoestore.core.orders.*
import io.spring.shoestore.core.security.StoreAuthProvider
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class OrderController(
    private val orderProcessingService: OrderProcessingService,
    private val orderQueryService: OrderQueryService,
    private val storeAuthProvider: StoreAuthProvider,
    private val authenticationUtils: AuthenticationUtils,
    ) {

    @GetMapping("/orders")
    fun listOrdersForUser(authentication: Authentication?, model: Model): String {
        val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
        if (userDetails == null) {
            log.warn("User not authenticated. Redirecting to login page.")
            return "login"
        }
        log.info("Fetching orders for user ${userDetails.getUserID()}")
        val foundOrders = orderQueryService.retrieveOrdersForUser(userDetails.getUserID())

//        val testOrder = orderQueryService.retrieveOrderById(UUID.fromString("49aea874-ce9e-4cb1-82d2-47bf08194251"));
//        log.info("testOrder: ${testOrder?.order?.getItemDetails()}")

        model.addAttribute("orders", foundOrders)

        return "orders"
    }


    @GetMapping("/order/{id}")
    fun getOrderById(@PathVariable id: String, authentication: Authentication?, model: Model): String {
        val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)

        if (userDetails == null) {
            log.warn("User not authenticated. Redirecting to login page.")
            return "login"
        }

        try {
            val orderId = UUID.fromString(id)
            val order = orderQueryService.retrieveOrderById(orderId)

            if (order == null) {
                log.warn("Order with ID $id not found.")
                model.addAttribute("errorMessage", "Order not found.")
                return "error" // or redirect to an appropriate error page
            }

            log.info("Retrieved order: $order")
            model.addAttribute("order", order.order)
            model.addAttribute("payments", order.payments)

            return "order-details"
        } catch (e: IllegalArgumentException) {
            log.warn("Invalid order ID format: $id")
            model.addAttribute("error", "Invalid order ID.")
            return "error"
        }
    }

//    @PostMapping("/place-order")
//    fun placeOrder(
//        authentication: Authentication?,
//        model: Model,
//        @ModelAttribute shippingDetails: ShippingDetails,
//        paymentMethod: String,
//        bindingResult: BindingResult,
//        redirectAttributes: RedirectAttributes
//    ): String {
//        try {
//            val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
//
//            if (userDetails == null) {
//                log.warn("User not authenticated. Redirecting to login page.")
//                return "login"
//            }
//
//            // Validate ShippingDetails
//            val shippingErrors = shippingDetails.validate()
//
//            for (error in shippingErrors) {
//                bindingResult.rejectValue(error.first, "error.shippingDetails", error.second)
//            }
//
//            if (bindingResult.hasErrors()) {
//                log.error("Validation errors: ${bindingResult.allErrors}");
//
//                // Add attributes to redirectAttributes
//                redirectAttributes.addFlashAttribute("shippingDetails", shippingDetails);
//                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.shippingDetails", bindingResult);
//
//                return "redirect:/checkout";
//            }
//
//            val userId = userDetails.getUserID()
//            val command = PlaceOrderCommand(userId, null, shippingDetails, paymentMethod)
//
//            val result = orderProcessingService.placeOrder(command)
//            return when (result) {
//                is OrderSuccess -> {
//                    model.addAttribute("orderSuccess", true)
//                    model.addAttribute("orderId", result.orderId)
//                    model.addAttribute("orderDate", result.orderDate)
//                    model.addAttribute("totalAmount", result.totalAmount)
//                    "orderConfirmation"
//                }
//                is OrderFailure -> {
//                    model.addAttribute("orderSuccess", false)
//                    model.addAttribute("errorMessage", result.reason)
//                    "orderError"
//                }
//            }
//        } catch (e: StockInsufficientException) {
//            log.error("Stock insufficient error", e)
//            throw e  // Re-throw the exception
//        } catch (e: Exception) {
//            log.error("Error occurred", e)
//            return "redirect:/error"
//        }
//    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderController::class.java)
    }
}