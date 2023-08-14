package io.spring.shoestore.app.http.api

import java.util.UUID

data class UserResults(val users: List<UserData>)

data class UserData(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val roleID: String
)
