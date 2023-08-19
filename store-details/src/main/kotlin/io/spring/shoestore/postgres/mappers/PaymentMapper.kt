package io.spring.shoestore.postgres.mappers

import io.spring.shoestore.core.orders.Payment
import io.spring.shoestore.core.orders.PaymentStatus
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.*

internal class PaymentMapper: RowMapper<Payment> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Payment {
        return Payment(
            id = UUID.fromString(rs.getString("id")),
            orderID = UUID.fromString(rs.getString("orderID")),
            paymentDate = rs.getTimestamp("paymentDate")?.toInstant(),
            paymentMethod = rs.getString("paymentMethod"),
            totalAmount = rs.getBigDecimal("totalAmount"),
            paymentStatus = PaymentStatus.lookup(rs.getString("paymentStatus")) ?: throw IllegalArgumentException("Invalid payment status in database")
        )
    }
}