package io.spring.shoestore.core.users

import java.util.*

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
        private val DEFAULT_UUID_STRING = "00000000-0000-0000-0000-000000000000"
        @JvmStatic
        fun from(rawValue: String?): UserId {
            return if (rawValue == null) {
                UserId(UUID.fromString(DEFAULT_UUID_STRING))
            } else {
                UserId(UUID.fromString(rawValue))
            }
        }
    }
}
