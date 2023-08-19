package io.spring.shoestore.postgres.mappers

import io.spring.shoestore.core.cart.Cart
import io.spring.shoestore.core.cart.CartId
import io.spring.shoestore.core.cart.SessionId
import io.spring.shoestore.core.users.UserId
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.*

internal class CartMapper(
) : RowMapper<Cart> {
    val productMapper = ProductMapper()

    override fun mapRow(rs: ResultSet, rowNum: Int): Cart {
        return Cart(
            id = CartId(UUID.fromString(rs.getString("id"))),
            sessionID = SessionId(UUID.fromString(rs.getString("sessionID"))),
            userID = UserId(UUID.fromString(rs.getString("userID"))),
            sku = rs.getString("sku"),
            product = productMapper.mapRow(rs, rowNum),
            label = rs.getString("label"),
            size = rs.getString("size"),
            color = rs.getString("color"),
            quantity = rs.getInt("quantity"),
            quantityAvailable = rs.getInt("quantityAvailable"),
            isDelete = rs.getBoolean("isDelete")
        )
    }
}
