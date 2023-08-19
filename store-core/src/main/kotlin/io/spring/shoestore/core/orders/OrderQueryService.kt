package io.spring.shoestore.core.orders

import io.spring.shoestore.core.users.UserId
import java.util.*

class OrderQueryService(private val orderRepository: OrderRepository) {
    fun retrieveOrdersForUser(userId: UserId): List<Order> {
        return orderRepository.listOrdersForUser(userId)
    }

    fun retrieveOrderById(orderId: UUID): OrderWithPayment? {
        return orderRepository.getOrderById(orderId)
    }
}
