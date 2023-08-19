package io.spring.shoestore.postgres.mappers

import io.spring.shoestore.core.products.Category
import io.spring.shoestore.core.products.CategoryId
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.products.ProductId
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.*

internal class ProductMapper: RowMapper<Product> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Product {
        return Product(
            ProductId(UUID.fromString(rs.getString("id"))),
            CategoryId(UUID.fromString(rs.getString("categoryID"))),
            rs.getString("categoryName"),
            rs.getString("productCode"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("imageURL"),
            rs.getBigDecimal("price"),
        )
    }
}

class CategoryMapper : RowMapper<Category> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Category {
        return Category(
            id = CategoryId.from(rs.getString("id")),
            name = rs.getString("name")
        )
    }
}
