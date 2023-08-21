package io.spring.shoestore.postgres.mappers

import io.spring.shoestore.core.orders.Order
import io.spring.shoestore.core.products.Product
import org.springframework.jdbc.core.RowMapper
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.*

internal class OrderMapper: RowMapper<Order> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Order {
        return Order(
            id = UUID.fromString(rs.getString("id")),
            userID = UUID.fromString(rs.getString("userID")),
            orderDate = rs.getTimestamp("orderDate").toInstant(),
            orderStatus = Order.OrderStatus.lookup(rs.getString("status")) ?: throw IllegalArgumentException("Invalid order status in database"),
            receiverName = rs.getString("receiverName"),
            receiverPhone = rs.getString("receiverPhone"),
            note = rs.getString("note"),
            shippingStreet = rs.getString("shippingStreet"),
            shippingWard = rs.getString("shippingWard"),
            shippingDistrict = rs.getString("shippingDistrict"),
            shippingProvince = rs.getString("shippingProvince"),
            productCost = rs.getBigDecimal("productCost"),
            taxAmount = rs.getBigDecimal("taxAmount"),
            shippingCost = rs.getBigDecimal("shippingCost"),
            totalAmount = rs.getBigDecimal("totalAmount"),
            shippingDate = rs.getTimestamp("shippingDate")?.toInstant(),
            carrier = rs.getString("carrier"),
            trackingNumber = rs.getString("trackingNumber")
        )
    }
}

internal data class LineItemRow(
    val orderId: UUID,
    val sku: String,
    val quantity: Int,
    val pricePerItem: BigDecimal,
    val subtotal: BigDecimal
)
internal data class LineItemDetailsRow(
    val orderId: UUID,
    val sku: String,
    val quantity: Int,
    val product: Product?,
    val label: String?,
    val size: String?,
    val color:  String?,
    val pricePerItem: BigDecimal,
    val subtotal: BigDecimal
)
internal class LineItemMapper: RowMapper<LineItemRow> {
    override fun mapRow(rs: ResultSet, rowNum: Int): LineItemRow {
        return LineItemRow(
            orderId = UUID.fromString(rs.getString("orderID")),
            sku = rs.getString("sku"),
            quantity = rs.getInt("quantity"),
            pricePerItem = rs.getBigDecimal("pricePerItem"),
            subtotal = rs.getBigDecimal("subtotal")
        )
    }
}

internal class LineItemDetailsMapper: RowMapper<LineItemDetailsRow> {
    val productMapper = ProductMapper()
    override fun mapRow(rs: ResultSet, rowNum: Int): LineItemDetailsRow {
        return LineItemDetailsRow(
            orderId = UUID.fromString(rs.getString("orderID")),
            sku = rs.getString("sku"),
            quantity = rs.getInt("quantity"),
            product = productMapper.mapRow(rs, rowNum),
            label = rs.getString("label"),
            size = rs.getString("size"),
            color = rs.getString("color"),
            pricePerItem = rs.getBigDecimal("pricePerItem"),
            subtotal = rs.getBigDecimal("subtotal")
        )
    }
}
