
import io.spring.shoestore.core.exceptions.RepositoryException
import io.spring.shoestore.core.users.User
import io.spring.shoestore.core.users.UserId
import io.spring.shoestore.core.users.UserRepository
import io.spring.shoestore.core.users.UserUpdate
import io.spring.shoestore.postgres.mappers.UserMapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate

class PostgresUserRepository(private val jdbcTemplate: JdbcTemplate) : UserRepository {
    private val userMapper = UserMapper()
    private val logger = LoggerFactory.getLogger(PostgresUserRepository::class.java)

    override fun findByUsername(username: String): User? {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM tbl_User WHERE username = ? AND isDelete = FALSE LIMIT 1;",
            userMapper,
            username
        )
    }
    override fun findByEmail(email: String): User? {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM tbl_User WHERE email = ? AND isDelete = FALSE LIMIT 1;",
            userMapper,
            email
        )
    }
    override fun findById(id: UserId): User? {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM tbl_User WHERE id = ? AND isDelete = FALSE LIMIT 1;",
            userMapper,
            id.value // Convert UUID to String
        )
    }

    override fun list(): List<User> {
        return jdbcTemplate.query(
            "SELECT * FROM tbl_User WHERE isDelete = FALSE;",
            userMapper
        )
    }

    override fun findByName(namePartial: String): List<User> {
        return jdbcTemplate.query(
            "SELECT * FROM tbl_User WHERE fullName ILIKE ? AND isDelete = FALSE;",
            userMapper,
            "%$namePartial%"
        )
    }
    override fun save(user: User): User {
        try {
            jdbcTemplate.update(
                "INSERT INTO tbl_User (id, username, email, fullName, password, roleID, status, isDelete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                user.id.value, user.username, user.email, user.fullName, user.password, user.roleID, user.status, user.isDelete
            )
            return user
        } catch (e: Exception) {
            val errorMessage = "Database error while saving user: ${e.message}"
            throw RepositoryException(errorMessage)
        }
    }
    // Update an existing user's details
    override fun update(user: UserUpdate): Boolean {
        val rowsAffected = jdbcTemplate.update(
            "UPDATE tbl_User SET fullName = ?, roleID = ?, status = ? " +
                    "WHERE id = ?;",
            user.fullName, user.roleID, user.status,
            user.id.value
        )
        return rowsAffected > 0
    }

    // Delete a user by ID
    override fun deleteById(id: UserId): Boolean {
        val rowsAffected = jdbcTemplate.update(
            "UPDATE tbl_User SET isDelete = TRUE WHERE id = ?;",
            id.value
        )
        return rowsAffected > 0
    }


}
