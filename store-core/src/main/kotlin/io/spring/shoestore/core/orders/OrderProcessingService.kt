package io.spring.shoestore.core.orders

import PlaceOrderCommand
import io.spring.shoestore.core.cart.CartService
import io.spring.shoestore.core.cart.SessionId
import org.slf4j.LoggerFactory
import java.util.*

class OrderProcessingService(
    private val cartService: CartService,
    private val orderRepository: OrderRepository

) {
    fun placeOrder(command: PlaceOrderCommand): OrderResult {
        val order = convertCartToOrder(
            cartService.getCartItems(
                sessionID = SessionId.from(null),
                userID = command.userId
            ), command.userId, command.orderId
        )

        // Check if the order is empty
        if (order.getItems().isEmpty()) {
            return OrderFailure("Cannot place an order with an empty cart.")
        }

        log.info("Total cost of ${order.getItems().size} items is ${order.price}")

        order.updateShippingInfo(command.shippingDetails)

        val isOrderSubmitted = orderRepository.submitOrder(order, command.paymentMethod)

        if (isOrderSubmitted) {
            try {
                cartService.clearCart(
                    sessionID = SessionId.from(null),
                    command.userId
                )
            } catch (e: Exception) {

            }
            return OrderSuccess(order.id, order.price.toString(), order.orderDate)
        } else {
            return OrderFailure("Failed to submit the order.")
        }
    }
    fun updatePaymentAndOrderStatus(
        paymentId: UUID,
        paymentStatus: PaymentStatus,
        order: Order,
        reduceProduct: Boolean
    ): OrderResult {
        try {
            val isUpdated = orderRepository.updatePaymentAndOrderStatus(paymentId, paymentStatus, order, reduceProduct)
            if (isUpdated) {
                log.info("Successfully updated payment ID: $paymentId to status: $paymentStatus and order ID: ${order.id} to status: ${order.orderStatus.description}")
                val order = orderRepository.getOrderById(order.id);
                if (order != null) {
                    return OrderSuccess(order.order.id, order.order.totalAmount.toString(), order.order.orderDate)
                } else {
                    return OrderFailure("Failed to get the order after update.")
                }
            } else {
                log.warn("Failed to update payment ID: $paymentId and/or order ID: ${order.id}")
                return OrderFailure("Failed to submit the order.")
            }
        } catch (e: Exception) {
            log.error("Error while updating payment and order statuses in service", e)
            return OrderFailure("Failed to submit the order.")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderProcessingService::class.java)
    }
}
