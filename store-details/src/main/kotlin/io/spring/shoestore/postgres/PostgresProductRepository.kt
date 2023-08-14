package io.spring.shoestore.postgres

import io.spring.shoestore.core.products.*
import io.spring.shoestore.postgres.mappers.ProductMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal
import java.util.UUID

class PostgresProductRepository(private val jdbcTemplate: JdbcTemplate) : ProductRepository {

    private val productMapper = ProductMapper()

    override fun findById(id: ProductId): Product? {
        return jdbcTemplate.queryForObject(
            "select * from tbl_Product where id = ? limit 1;",
            productMapper,
            id.value.toString() // Convert UUID to String
        )
    }

    override fun list(): List<Product> {
        return jdbcTemplate.query("select * from tbl_Product;", productMapper)
    }

    override fun findByName(namePartial: String): List<Product> {
        return jdbcTemplate.query(
            "select * from tbl_Product where name ilike ?",
            productMapper,
            "%$namePartial%"
        )
    }

    override fun findByPriceUnder(upperPrice: BigDecimal): List<Product> {
        return jdbcTemplate.query(
            "select * from tbl_Product where price <= ?",
            productMapper,
            upperPrice
        )
    }
}
