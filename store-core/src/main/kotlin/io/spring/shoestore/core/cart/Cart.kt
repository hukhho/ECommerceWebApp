package io.spring.shoestore.core.cart

import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.users.UserId
import java.util.*

data class Cart(
    val id: CartId,
    val sessionID: SessionId?,
    val userID: UserId?,
    val sku: String,
    val product: Product?,
    val label: String?,
    val size: String?,
    val color: String?,
    val quantity: Int,
    val quantityAvailable: Int,
    val isDelete: Boolean = false
)

data class CartId(val value: UUID) {
    companion object {
        @JvmStatic
        fun from(rawValue: String): CartId {
            return CartId(UUID.fromString(rawValue))
        }
    }
}
data class SessionId(val value: UUID) {
    companion object {
        private val DEFAULT_UUID_STRING = "00000000-0000-0000-0000-000000000000"
        @JvmStatic
        fun from(rawValue: String?): SessionId {
            return if (rawValue == null) {
                SessionId(UUID.fromString(DEFAULT_UUID_STRING))
            } else {
                SessionId(UUID.fromString(rawValue))
            }
        }
    }
}
