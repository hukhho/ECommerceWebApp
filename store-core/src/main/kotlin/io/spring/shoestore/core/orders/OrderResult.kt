package io.spring.shoestore.core.orders

import java.time.Instant
import java.util.*

sealed interface OrderResult

data class OrderFailure(val reason: String) : OrderResult
data class OrderSuccess(val orderId: UUID,
                        val totalAmount: String,
                        val orderDate: Instant) : OrderResult
