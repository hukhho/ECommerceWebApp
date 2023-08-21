package io.spring.shoestore.postgres

import io.spring.shoestore.core.products.ProductId
import io.spring.shoestore.core.variants.ProductVariant
import io.spring.shoestore.core.variants.ProductVariantRepository
import io.spring.shoestore.postgres.mappers.ProductVariantMapper
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.PreparedStatement

class PostgresProductVariantRepository(private val jdbcTemplate: JdbcTemplate): ProductVariantRepository {

    override fun findAllVariantsForProduct(productId: ProductId): List<ProductVariant> {
        return jdbcTemplate.query("SELECT * FROM tbl_product_variation " +
                "WHERE productID = ? AND isDelete = FALSE;",
            PV_MAPPER,
            productId.value
        )
    }

    override fun findById(sku: String): ProductVariant? {
        return jdbcTemplate.queryForObject("SELECT * FROM tbl_product_variation " +
                "WHERE sku = ? AND isDelete = FALSE limit 1;",
            PV_MAPPER,
            sku)
    }
    override fun registerNewVariants(variants: List<ProductVariant>): Boolean {
        val affectedRows = jdbcTemplate.batchUpdate("INSERT INTO tbl_product_variation (sku, productID, label, size, color, quantityavailable) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON conflict do nothing;", object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val variant = variants[i]
                ps.setString(1, variant.sku)
                ps.setObject(2, variant.productId.value)
                ps.setString(3, variant.label)
                ps.setString(4, variant.size.code)
                ps.setString(5, variant.color.code)
                ps.setInt(6, variant.quantityAvailable)
            }
            override fun getBatchSize(): Int = variants.size
        })
        return affectedRows.any { it > 0 }
    }

    override fun updateVariant(variant: ProductVariant): Boolean {
        val affectedRows = jdbcTemplate.update(
            "UPDATE tbl_product_variation SET label = ?, size = ?, color = ?, quantityavailable = ? " +
                    "WHERE sku = ? AND isDelete = FALSE;",
            variant.label,
            variant.size.code,
            variant.color.code,
            variant.quantityAvailable,
            variant.sku
        )
        return affectedRows > 0
    }

    override fun deleteVariant(sku: String): Boolean {
        val affectedRows = jdbcTemplate.update(
            "UPDATE tbl_product_variation SET isDelete = TRUE " +
                    " WHERE sku = ?;",
            sku
        )
        return affectedRows > 0
    }



    companion object {
        private val PV_MAPPER = ProductVariantMapper()
    }

}