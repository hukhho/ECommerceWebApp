package io.spring.shoestore.core.users

class UserService(private val repository: UserRepository) {

    // parse query
    fun getByUsername(username: String): User? = repository.findByUsername(username)

    fun get(id: UserId): User? = repository.findById(id)

    fun search(query: UserLookupQuery): List<User> {
        if (query.isEmpty()) {
            return repository.list()
        } else {
            val nameResults = repository.findByName(query.byName ?: "")
            // todo: price
            return nameResults
        }
    }
}





