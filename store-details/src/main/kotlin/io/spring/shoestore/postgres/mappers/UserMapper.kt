package io.spring.shoestore.postgres.mappers

import io.spring.shoestore.core.products.*
import io.spring.shoestore.core.users.User
import io.spring.shoestore.core.users.UserId
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.UUID


internal class UserMapper : RowMapper<User> {
    override fun mapRow(rs: ResultSet, rowNum: Int): User {
        return User(
            UserId(UUID.fromString(rs.getString("id"))),
            username = rs.getString("username"),
            email = rs.getString("email"),
            fullName = rs.getString("fullName"),
            password = rs.getString("password"),
            roleID = rs.getString("roleID"),
            status = rs.getBoolean("status"),
            isDelete = rs.getBoolean("isDelete")
        )
    }
}
