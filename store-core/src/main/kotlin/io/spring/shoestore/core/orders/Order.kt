package io.spring.shoestore.core.orders

import ShippingDetails
import io.spring.shoestore.core.cart.Cart
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.users.UserId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class Order(
    val id: UUID, // Immutable: Order ID should never change
    val userID: UUID,  //CONSTRUCTOR HERE
    val orderDate: Instant = Instant.now(), // Immutable: The date the order was placed shouldn't change
    var orderStatus: OrderStatus = OrderStatus.PENDING, // Mutable: The status can change (e.g., from PENDING to SHIPPED)
    var receiverName: String? = null, // Mutable: Receiver's name might be updated
    var receiverPhone: String? = null, // Mutable: Receiver's phone might be updated
    var note: String? = null,         // Mutable: Notes can be added or updated
    var shippingStreet: String? = null, // Mutable: Shipping address might be updated
    var shippingWard: String? = null, // Mutable: Shipping ward might be updated
    var shippingDistrict: String? = null, // Mutable: Shipping district might be updated
    var shippingProvince: String? = null, // Mutable: Shipping province might be updated
    var productCost: BigDecimal = BigDecimal.ZERO, // Immutable: Product cost at the time of order shouldn't change
    val taxAmount: BigDecimal = BigDecimal.ZERO,   // Immutable: Tax amount at the time of order shouldn't change
    val shippingCost: BigDecimal = BigDecimal.ZERO, // Immutable: Shipping cost at the time of order shouldn't change
    var totalAmount: BigDecimal = BigDecimal.ZERO,
    var shippingDate: Instant? = null, // Mutable: Shipping date might be updated when the order is shipped
    var carrier: String? = null,       // Mutable: Carrier might be updated
    var trackingNumber: String? = null // Mutable: Tracking number might be updated when available
)
{
    private val items: MutableList<OrderLineItem> = mutableListOf()
    private val itemsWithDetails: MutableList<OrderLineItemResponse> = mutableListOf()

    var price: BigDecimal = BigDecimal.ZERO
        private set

    fun getItems() = items
    fun getItemDetails() = itemsWithDetails

    fun addItem(sku: String, pricePer: BigDecimal, quantity: Int) {
        val subtotal = pricePer.multiply(BigDecimal(quantity))
        items.add(OrderLineItem(id, sku, quantity, subtotal))
        price = price.add(subtotal)
    }
    fun addItemDetails(
        sku: String,
        product: Product?,
        label: String?,
        size: String?,
        color: String?,
        pricePer: BigDecimal,
        quantity: Int
    ) {
        val subtotal = pricePer.multiply(BigDecimal(quantity))
        items.add(OrderLineItem(id, sku, quantity, subtotal))
        itemsWithDetails.add(
            OrderLineItemResponse(
                orderID = id,
                sku = sku,
                product = product,
                label = label,
                size = size,
                color = color,
                quantity = quantity,
                subtotal = subtotal
            )
        )
        price = price.add(subtotal)
    }

    fun addNote(newNote: String) {
        if (orderStatus in listOf(OrderStatus.PENDING, OrderStatus.PROCESSING)) {
            note = newNote
        } else {
            throw Exception("Cannot add note. Order is in ${orderStatus.description} status.")
        }
    }

    fun updateReceiverInfo(newName: String?, newPhone: String?) {
        if (orderStatus in listOf(OrderStatus.PENDING, OrderStatus.PROCESSING)) {
            receiverName = newName
            receiverPhone = newPhone
        } else {
            throw Exception("Cannot update receiver info. Order is in ${orderStatus.description} status.")
        }
    }

    fun updateShippingInfo(shippingDetails: ShippingDetails) {
        if (orderStatus in listOf(OrderStatus.PENDING, OrderStatus.PROCESSING)) {
            receiverName = shippingDetails.receiverName
            receiverPhone = shippingDetails.receiverPhone
            note = shippingDetails.note
            shippingStreet = shippingDetails.shippingStreet
            shippingWard = shippingDetails.shippingWard
            shippingDistrict = shippingDetails.shippingDistrict
            shippingProvince = shippingDetails.shippingProvince
        } else {
            throw Exception("Cannot update shipping info. Order is in ${orderStatus.description} status.")
        }
    }
    fun commentOrder(comment: String) {
        // Append comments, and not overwrite the existing note.
        note = "$note; $comment"
    }

    fun updateOrderStatus(newStatus: OrderStatus) {
        orderStatus = newStatus
    }

    fun cancelOrder() {
        if (orderStatus in listOf(OrderStatus.PENDING, OrderStatus.PROCESSING)) {
            orderStatus = OrderStatus.CANCELLED
        } else {
            throw Exception("Cannot cancel order. Order is in ${orderStatus.description} status.")
        }
    }

    fun refundOrder() {
        if (orderStatus in listOf(OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.COMPLETED)) {
            orderStatus = OrderStatus.REFUNDED
        } else {
            throw Exception("Cannot refund order. Order is in ${orderStatus.description} status.")
        }
    }

    fun shipOrder(carrierName: String, trackingNo: String) {
        if (orderStatus == OrderStatus.PROCESSING) {
            orderStatus = OrderStatus.SHIPPED
            carrier = carrierName
            trackingNumber = trackingNo
            shippingDate = Instant.now()
        } else {
            throw Exception("Cannot ship order. Order is in ${orderStatus.description} status.")
        }
    }

    fun assignCarrier(carrierName: String) {
        if (orderStatus in listOf(OrderStatus.PENDING, OrderStatus.PROCESSING)) {
            carrier = carrierName
        } else {
            throw Exception("Cannot assign carrier. Order is in ${orderStatus.description} status.")
        }
    }

    fun updateTrackingNumber(trackingNo: String) {
        if (orderStatus == OrderStatus.SHIPPED) {
            trackingNumber = trackingNo
        } else {
            throw Exception("Cannot update tracking number. Order is not in SHIPPED status.")
        }
    }
    override fun toString(): String {
        return "Order(id=$id, userID=$userID, orderDate=$orderDate, orderStatus=$orderStatus, receiverName=$receiverName, receiverPhone=$receiverPhone, note=$note, shippingStreet=$shippingStreet, shippingWard=$shippingWard, shippingDistrict=$shippingDistrict, shippingProvince=$shippingProvince, productCost=$productCost, taxAmount=$taxAmount, shippingCost=$shippingCost, totalAmount=$totalAmount, shippingDate=$shippingDate, carrier=$carrier, trackingNumber=$trackingNumber, items=$items, price=$price)"
    }

    data class OrderLineItem(
        val orderID: UUID,
        val sku: String,
        val quantity: Int,
        val subtotal: BigDecimal
    )

    data class OrderLineItemResponse(
        val orderID: UUID,
        val sku: String,
        val product: Product?,
        val label: String?,
        val size: String?,
        val color:  String?,
        val quantity: Int,
        val subtotal: BigDecimal
    )
    enum class OrderStatus(val description: String) {
        PENDING("Pending"),
        PROCESSING("Processing"),
        ON_HOLD("On Hold"),
        SHIPPED("Shipped"),
        DELIVERED("Delivered"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled"),
        REFUNDED("Refunded"),
        FAILED("Failed"),
        BACKORDERED("Backordered"),
        RETURNED("Returned");
        companion object {
            fun lookup(description: String): OrderStatus? {
                return OrderStatus.values().find { it.description == description }
            }
        }
    }
}

fun convertCartToOrder(cartItems: List<Cart>, userID: UserId, orderID: UUID): Order {

    val order = Order(id = orderID, userID = userID.value)
    var totalAmount = BigDecimal.ZERO

    for (cartItem in cartItems) {
        if (cartItem.quantity > cartItem.quantityAvailable) {
            throw StockInsufficientException("Not enough stock for SKU: ${cartItem.sku}")
        }

        val itemPrice = cartItem.product?.price ?: BigDecimal.ZERO

        totalAmount += itemPrice.multiply(BigDecimal(cartItem.quantity))

        order.addItem((cartItem.sku), itemPrice, cartItem.quantity)
    }

    order.totalAmount = totalAmount
    order.productCost = totalAmount
    
    return order
}
