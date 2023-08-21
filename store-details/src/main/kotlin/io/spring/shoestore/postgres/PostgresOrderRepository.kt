package io.spring.shoestore.postgres

import io.spring.shoestore.core.orders.*
import io.spring.shoestore.core.users.UserId
import io.spring.shoestore.postgres.mappers.*
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.util.*


open class PostgresOrderRepository(private val jdbcTemplate: JdbcTemplate): OrderRepository {

    private val orderMapper = OrderMapper()
    private val lineItemMapper = LineItemMapper()
    private val lineItemDetailsMapper = LineItemDetailsMapper()

    private val paymentMapper = PaymentMapper()

    @Transactional
    override fun submitOrder(order: Order, paymentMethod: String): Boolean {
        return try {
            // 1. Determine the Products and Quantities Ordered
            val orderedItems = order.getItems()

            // 2. Update the Product Variant Table
            for (item in orderedItems) {
                val sku = item.sku
                val orderedQuantity = item.quantity

                // Check if there's enough stock
                val currentQuantity = jdbcTemplate.queryForObject(
                    "SELECT quantityAvailable FROM tbl_Product_Variation WHERE sku = ?",
                    Int::class.java,
                    sku
                ) ?: 0

                if (currentQuantity < orderedQuantity) {
                    throw StockInsufficientException("Not enough stock for SKU: $sku")
                }


                // Decrease the quantityAvailable
//                jdbcTemplate.update(
//                    "UPDATE tbl_Product_Variation SET quantityAvailable = quantityAvailable - ? WHERE sku = ?",
//                    orderedQuantity,
//                    sku
//                )

            }

            // Persist the order
            jdbcTemplate.update(
                "INSERT INTO tbl_Order (id, userID, orderDate, status, receiverName, receiverPhone, note, shippingStreet, shippingWard, shippingDistrict, shippingProvince, productCost, taxAmount, shippingCost, totalAmount, shippingDate, carrier, trackingNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                order.id,
                order.userID,
                Timestamp.from(order.orderDate),
                order.orderStatus.description,
                order.receiverName,
                order.receiverPhone,
                order.note,
                order.shippingStreet,
                order.shippingWard,
                order.shippingDistrict,
                order.shippingProvince,
                order.productCost,
                order.taxAmount,
                order.shippingCost,
                order.totalAmount,
                order.shippingDate?.let { Timestamp.from(it) },
                order.carrier,
                order.trackingNumber
            )

            // Persist the line items
            jdbcTemplate.batchUpdate(
                "INSERT INTO tbl_Order_Item (orderID, sku, quantity, subtotal) VALUES (?, ?, ?, ?)",
                object : BatchPreparedStatementSetter {
                    override fun setValues(ps: PreparedStatement, i: Int) {
                        val lineItem = order.getItems()[i]
                        ps.setObject(1, order.id)
                        ps.setString(2, lineItem.sku)
                        ps.setInt(3, lineItem.quantity)
                        ps.setBigDecimal(4, lineItem.subtotal)
                    }

                    override fun getBatchSize(): Int = order.getItems().size
                }
            )
//            orderID UUID NULL,
//            paymentDate TIMESTAMP NULL,
//            paymentMethod VARCHAR(50) NULL,
//            totalAmount DECIMAL(10, 2) NOT NULL,
//            paymentStatus VARCHAR(50) NULL,
                jdbcTemplate.update(
                    "INSERT INTO tbl_Payment (orderID, paymentDate, paymentMethod, totalAmount, paymentStatus) " +
                            " VALUES(?, ?, ?, ?, ?) ",
                    order.id,
                    null,
                    paymentMethod,
                    order.totalAmount,
                    "Pending"
                )


            true
        } catch (e: Exception) {
            log.error("Error while submitting order", e)
            false
        }
    }

    @Transactional(readOnly = true)
    override fun listOrdersForUser(userId: UserId): List<Order> {
        try {
            // Fetch the orders for the user
            val orders = jdbcTemplate.query(
                "SELECT o.* FROM tbl_Order o WHERE o.userID = ?",
                orderMapper,
                userId.value
            )
            val lookup = orders.associateBy { it.id }

            val items = mutableListOf<LineItemRow>()

            lookup.keys.forEach { orderId ->
                items.addAll(
                    jdbcTemplate.query(
                        "SELECT  li.orderID,  li.sku, li.quantity, pv.label, pv.size, pv.color, p.*, p.price AS pricePerItem, li.subtotal" +
                                " FROM  tbl_Order_Item li" +
                                " JOIN  tbl_Product_Variation pv ON li.sku = pv.sku" +
                                " JOIN  tbl_Product p ON pv.productID = p.id" +
                                " WHERE  li.orderID = ? ORDER BY  li.orderID;",
                        lineItemMapper,
                        orderId
                    )
                )
            }

            // Add the line items to their corresponding orders
            items.forEach { li ->
                lookup[li.orderId]?.let { order ->
                    order.addItem(sku = li.sku, pricePer = li.pricePerItem, quantity = li.quantity)
                }
            }
            return orders
        } catch (e: Exception) {
            log.error("Error while fetching orders for user", e)
            throw e
        }
    }


    @Transactional(readOnly = true)
    override fun getOrderById(orderId: UUID): OrderWithPayment? {
        try {
            // Fetch the order by its ID
            val order = jdbcTemplate.queryForObject(
                "SELECT o.* FROM tbl_Order o WHERE o.id = ?",
                orderMapper,
                orderId
            ) ?: return null

            // Fetch the line items for the order
            val itemDetails = jdbcTemplate.query(
                "SELECT li.orderID, li.sku, li.quantity, p.price AS pricePerItem, li.subtotal," +
                        " p.*, pv.*, cat.name AS categoryName" +
                        " FROM tbl_Order_Item li" +
                        " JOIN tbl_Product_Variation pv ON li.sku = pv.sku" +
                        " JOIN tbl_Product p ON pv.productID = p.id" +
                        " JOIN tbl_Category cat ON p.categoryID = cat.id " +
                        "WHERE li.orderID = ? " +
                        "ORDER BY li.orderID;",
                lineItemDetailsMapper,
                orderId
            )

            itemDetails.forEach { li ->
                order.addItemDetails(
                    sku = li.sku,
                    product = li.product,
                    label = li.label,
                    size = li.size,
                    color = li.color,
                    pricePer = li.pricePerItem,
                    quantity = li.quantity
                )
            }

            // Fetch the payment details for the order
            val payments = jdbcTemplate.query(
                "SELECT p.* FROM tbl_Payment p WHERE p.orderID = ?",
                paymentMapper,
                orderId
            )

            return OrderWithPayment(order, payments)
        } catch (e: Exception) {
            log.error("Error while fetching order by ID", e)
            throw e
        }
    }


    override fun removeAllOrders() {
        jdbcTemplate.update("delete from order_line_items;")
        jdbcTemplate.update("delete from orders;")
    }

    override fun getCurrentInventory(sku: String): Int {
        return jdbcTemplate.queryForObject(
            "SELECT quantityAvailable FROM tbl_Product_Variation WHERE sku = ?",
            Int::class.java,
            sku
        ) ?: 0
    }

    override fun updateInventory(sku: String, quantity: Int): Boolean {
        return jdbcTemplate.update(
            "UPDATE tbl_Product_Variation SET quantityAvailable = quantityAvailable + ? WHERE sku = ?",
            quantity,
            sku
        ) > 0
    }

    @Transactional
    override fun updatePaymentAndOrderStatus(paymentId: UUID, paymentStatus: PaymentStatus, order: Order, reduceProduct: Boolean): Boolean {
        return try {

            val updatedPaymentRows = jdbcTemplate.update(
                "UPDATE tbl_Payment SET paymentStatus = ? WHERE id = ?",
                paymentStatus.description,
                paymentId
            )

            val updatedOrderRows = jdbcTemplate.update(
                "UPDATE tbl_Order SET status = ? WHERE id = ?",
                order.orderStatus.description,
                order.id
            )

            val orderedItems = order.getItems()
            // 2. Update the Product Variant Table
            var quantityAvailableRows = 0

            for (item in orderedItems) {
                val sku = item.sku
                val orderedQuantity = item.quantity
                // Check if there's enough stock
                val currentQuantity = jdbcTemplate.queryForObject(
                    "SELECT quantityAvailable FROM tbl_Product_Variation WHERE sku = ?",
                    Int::class.java,
                    sku
                ) ?: 0
                if (currentQuantity < orderedQuantity) {
                    throw StockInsufficientException("Not enough stock for SKU: $sku")
                }
                //Decrease the quantityAvailable
                if(reduceProduct){
                    quantityAvailableRows += jdbcTemplate.update(
                        "UPDATE tbl_Product_Variation SET quantityAvailable = quantityAvailable - ? WHERE sku = ?",
                        orderedQuantity,
                        sku
                    )
                } else {
                    quantityAvailableRows = 1
                }
            }
            updatedPaymentRows > 0 && updatedOrderRows > 0 && quantityAvailableRows > 0
        } catch (e: Exception) {
            log.error("Error while updating payment and order statuses and decrease the quantityAvailable", e)
            false
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PostgresOrderRepository::class.java)
    }
}