package io.spring.shoestore.core.users

import java.util.*
interface Validator<T> {
    fun validate(data: T): List<Pair<String, String>>
}
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
data class UserUpdate(
    val id: UserId,
    val username: String,
    val email: String,
    val fullName: String,
    val roleID: String,
    val status: Boolean
)
class UserUpdateValidator : Validator<UserUpdate> {
    override fun validate(data: UserUpdate): List<Pair<String, String>> {
        val errors = mutableListOf<Pair<String, String>>()

        // Validate fullName
        if (data.fullName.isBlank() ||  data.fullName.length < 3) {
            errors.add(Pair("fullName", "Full Name must be at least 3 characters long."))
        }

        // Validate roleID
        if (data.roleID != "AD" && data.roleID != "US") {
            errors.add(Pair("roleID", "Role ID must be either 'AD' or 'US'."))
        }

        return errors
    }
}
data class UserCreate(
    val username: String,
    val email: String,
    val fullName: String,
    val password: String,
    val confirmPassword: String,
    val roleID: String
)
class UserCreateValidator : Validator<UserCreate> {
    override fun validate(data: UserCreate): List<Pair<String, String>> {
        val errors = mutableListOf<Pair<String, String>>()

        // Validate username
        if (data.username.isBlank() || data.username.length < 3) {
            errors.add(Pair("username", "Username must be at least 3 characters long."))
        }

        // Validate email
        if (data.email.isBlank() || !data.email.contains("@")) {
            errors.add(Pair("email", "Invalid email format."))
        }

        // Validate fullName
        if (data.fullName.isBlank() ||  data.fullName.length < 3) {
            errors.add(Pair("fullName", "Full Name must be at least 3 characters long."))
        }

        // Validate password
        if (data.password.isBlank() || data.password.length < 6) {
            errors.add(Pair("password", "Password must be at least 6 characters long."))
        }

        // Validate confirmPassword
        if (data.password != data.confirmPassword) {
            errors.add(Pair("confirmPassword", "Password and Confirm Password must match."))
        }

        // Validate roleID
        if (data.roleID != "AD" && data.roleID != "US") {
            errors.add(Pair("roleID", "Role ID must be either 'AD' or 'US'."))
        }

        return errors
    }
}

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
