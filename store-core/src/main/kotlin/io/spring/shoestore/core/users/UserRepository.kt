package io.spring.shoestore.core.users

interface UserRepository {
    fun findById(id: UserId): User?
    fun list(): List<User>
    fun findByName(namePartial: String): List<User>
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
    fun update(user: UserUpdate): Boolean
    fun deleteById(id: UserId): Boolean
}