package io.spring.shoestore.core.users

interface UserRepository {
    fun findById(id: UserId): User?
    fun list(): List<User>
    fun findByName(namePartial: String): List<User>
    fun findByUsername(username: String): User? // Add this method
    fun save(user: User): User
}