package io.spring.shoestore.core.orders

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Payment(
    val id: UUID = UUID.randomUUID(),
    val orderID: UUID,
    val paymentDate: Instant? = null,
    val paymentMethod: String? = null,
    val totalAmount: BigDecimal = BigDecimal.ZERO,
    val paymentStatus: PaymentStatus
)

enum class PaymentMethod(val method: String) {
    MOMO("MOMO"),
    VNPAY("VNPAY"),
    COD("COD");

    companion object {
        fun lookup(method: String): PaymentMethod? {
            return PaymentMethod.values().find { it.method == method }
        }
    }
}
enum class PaymentStatus(val description: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded"),
    FAILED("Failed");
    companion object {
        fun lookup(description: String): PaymentStatus? {
            return PaymentStatus.values().find { it.description == description }
        }
    }
}

data class OrderWithPayment(
    val order: Order,
    val payments: List<Payment>
)
