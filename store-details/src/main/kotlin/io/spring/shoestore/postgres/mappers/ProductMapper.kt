package io.spring.shoestore.postgres.mappers

import io.spring.shoestore.core.products.*
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.UUID

internal class ProductMapper: RowMapper<Product> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Product {
        return Product(
            ProductId(UUID.fromString(rs.getString("id"))),
            UUID.fromString(rs.getString("categoryID")),
            rs.getString("productCode"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("imageURL"),
            rs.getBigDecimal("price"),
        )
    }
}