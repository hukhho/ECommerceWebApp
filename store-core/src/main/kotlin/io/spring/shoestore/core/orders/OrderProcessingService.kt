package io.spring.shoestore.core.orders

import PlaceOrderCommand
import io.spring.shoestore.core.cart.CartService
import io.spring.shoestore.core.cart.SessionId
import org.slf4j.LoggerFactory

class OrderProcessingService(
    private val cartService: CartService,
    private val orderRepository: OrderRepository

) {

    fun placeOrder(command: PlaceOrderCommand): OrderResult {
        val order = convertCartToOrder(cartService.getCartItems(
            sessionID = SessionId.from(null),
            userID = command.userId
        ), command.userId)

        // Check if the order is empty
        if (order.getItems().isEmpty()) {
            return OrderFailure("Cannot place an order with an empty cart.")
        }

        log.info("Total cost of ${order.getItems().size} items is ${order.price}")

        order.updateShippingInfo(command.shippingDetails)

        val isOrderSubmitted = orderRepository.submitOrder(order)

        if (isOrderSubmitted) {
            try {
                cartService.clearCart(sessionID = SessionId.from(null),
                    command.userId)
            } catch (e: Exception) {

            }
            return OrderSuccess(order.id, order.price.toString(), order.orderDate)
        } else {
            return OrderFailure("Failed to submit the order.")
        }
    }


    companion object {
        private val log = LoggerFactory.getLogger(OrderProcessingService::class.java)
    }
}
