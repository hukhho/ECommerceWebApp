package io.spring.shoestore.postgres

import io.spring.shoestore.core.products.ProductId
import io.spring.shoestore.core.variants.ProductVariant
import io.spring.shoestore.core.variants.ProductVariantRepository
import io.spring.shoestore.core.variants.Sku
import io.spring.shoestore.postgres.mappers.ProductVariantMapper
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.PreparedStatement

class PostgresProductVariantRepository(private val jdbcTemplate: JdbcTemplate): ProductVariantRepository {

    override fun findAllVariantsForProduct(productId: ProductId): List<ProductVariant> {
        return jdbcTemplate.query("select * from tbl_product_variation where productID = ?;",
            PV_MAPPER,
            productId.value
        )
    }

    override fun findById(sku: Sku): ProductVariant? {
        return jdbcTemplate.queryForObject("select * from tbl_product_variation where sku = ? limit 1;", PV_MAPPER, sku.value)
    }

    override fun registerNewVariants(variants: List<ProductVariant>) {
        jdbcTemplate.batchUpdate("insert into tbl_product_variation (sku, productID, label, size, color, quantityavailable) values (?, ?, ?, ?, ?, ?) on conflict do nothing; ", object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val variant = variants[i]
                ps.setString(1, variant.sku.value)
                ps.setObject(2, variant.productId.value)
                ps.setString(3, variant.label)
                ps.setString(4, variant.size.code)
                ps.setString(5, variant.color.code)
                ps.setInt(6, variant.quantityAvailable.value)
            }

            override fun getBatchSize(): Int = variants.size
        })
    }

    companion object {
        private val PV_MAPPER = ProductVariantMapper()
    }

}