package io.spring.shoestore.core.users

import io.spring.shoestore.core.products.ProductId
import java.math.BigDecimal
import java.util.UUID

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val fullName: String,
    val password: String,
    val roleID: String,
    val status: Boolean,
    val isDelete: Boolean = false
)
data class UserId(val value: UUID) {
    companion object {
        @JvmStatic
        fun from(rawValue: String): UserId {
            return UserId(UUID.fromString(rawValue))
        }
    }
}

