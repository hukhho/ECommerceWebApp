import io.spring.shoestore.core.users.User
import io.spring.shoestore.core.users.UserId
import io.spring.shoestore.core.users.UserRepository
import io.spring.shoestore.postgres.mappers.UserMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.util.UUID

class PostgresUserRepository(private val jdbcTemplate: JdbcTemplate) : UserRepository {

    private val userMapper = UserMapper()


    override fun findByUsername(username: String): User? {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM tbl_User WHERE username = ? LIMIT 1;",
            userMapper,
            username
        )
    }
    override fun findById(id: UserId): User? {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM tbl_User WHERE id = ? LIMIT 1;",
            userMapper,
            id.value.toString() // Convert UUID to String
        )
    }

    override fun list(): List<User> {
        return jdbcTemplate.query(
            "SELECT * FROM tbl_User;",
            userMapper
        )
    }

    override fun findByName(namePartial: String): List<User> {
        return jdbcTemplate.query(
            "SELECT * FROM tbl_User WHERE fullName ILIKE ?;",
            userMapper,
            "%$namePartial%"
        )
    }
    override fun save(user: User): User {
        jdbcTemplate.update(
            "INSERT INTO tbl_User (id, username, email, fullName, password, roleID, status, isDelete) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
            user.id, user.username, user.email, user.fullName, user.password, user.roleID, user.status, user.isDelete
        )
        return user
    }
}
