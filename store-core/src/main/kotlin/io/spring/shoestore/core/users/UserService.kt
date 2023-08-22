package io.spring.shoestore.core.users

import io.spring.shoestore.core.exceptions.RepositoryException
import io.spring.shoestore.core.exceptions.ServiceException
import org.slf4j.LoggerFactory

class UserService(private val repository: UserRepository) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    // parse query
    fun findByUsername(username: String): User? = repository.findByUsername(username)
    fun findByEmail(email: String): User? {
        return try {
            repository.findByEmail(email)
        } catch (ex: Exception) {
            null
        }
    }

    fun get(id: UserId): User? = repository.findById(id)

    fun search(query: UserLookupQuery): List<User> {
        if (query.isEmpty()) {
            return repository.list()
        } else {
            val nameResults = repository.findByName(query.byName ?: "")

            return nameResults
        }
    }

    fun save(user: User) {
        try {
            val userCheck = repository.findByEmail(user.email)

            if (userCheck != null) {
                if (userCheck.isDelete) {
                    val user = UserUpdate(userCheck.id, userCheck.username,
                        userCheck.email,
                        userCheck.fullName,
                        userCheck.roleID,
                        userCheck.status)
                    repository.update(user)
                }
            }

            logger.info("Attempting to save user: $user")
            repository.save(user)
            logger.info("User saved successfully: $user")
        } catch (e: RepositoryException) {
            val errorMessage = "Error saving user: ${e.message}"
            logger.error(errorMessage, e)
            throw ServiceException(errorMessage)
        } catch (e: Exception) {
            val errorMessage = "General error saving user: ${e.message}"
            logger.error(errorMessage, e)
            throw ServiceException(errorMessage)
        }
    }

    fun update(user: UserUpdate): Boolean? {
        try {
            logger.info("Updating user: $user")
            val updated = repository.update(user)
            if (updated) {
                logger.info("User updated successfully: $user")
            } else {
                logger.warn("Failed to update user: ${user.id}")
            }
            return updated
        } catch (e: RepositoryException) {
            val errorMessage = "Error update user: ${e.message}"
            logger.error(errorMessage, e)
            throw ServiceException(errorMessage)
        } catch (e: Exception) {
            val errorMessage = "Error update user: ${e.message}"
            logger.error("Error during user update: ${user.id}. Error: ${e.message}")
            throw ServiceException(errorMessage)
        }
    }

    fun deleteById(id: UserId): Boolean {
        return try {
            logger.info("Deleting user with ID: ${id.value}")
            val result = repository.deleteById(id)
            if (result) {
                logger.info("User with ID: ${id.value} deleted successfully")
            } else {
                logger.warn("No user found with ID: ${id.value}")
            }
            result
        } catch (e: Exception) {
            logger.error("Error deleting user with ID: ${id.value}. Error: ${e.message}", e)
            false
        }
    }

}





