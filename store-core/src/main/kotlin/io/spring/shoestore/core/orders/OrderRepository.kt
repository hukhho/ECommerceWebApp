package io.spring.shoestore.core.orders

import io.spring.shoestore.core.users.UserId
import java.util.*

interface OrderRepository {
    fun submitOrder(order: Order, paymentMethod: String): Boolean
    fun listOrdersForUser(userID: UserId): List<Order>
    fun removeAllOrders()
    fun getOrderById(orderId: UUID): OrderWithPayment?
    fun getCurrentInventory(sku: String): Int
    fun updateInventory(sku: String, quantity: Int): Boolean
    fun updatePaymentAndOrderStatus(
        paymentId: UUID,
        paymentStatus: PaymentStatus,
        order: Order,
        reduceProduct: Boolean
    ): Boolean
}
