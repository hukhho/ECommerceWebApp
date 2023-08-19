package io.spring.shoestore.postgres.mappers

import io.spring.shoestore.core.products.ProductId
import io.spring.shoestore.core.variants.*
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.*

internal class ProductVariantMapper: RowMapper<ProductVariant> {

    override fun mapRow(rs: ResultSet, rowNum: Int): ProductVariant {
        val sku = Sku(rs.getString("sku"))
        val productId = ProductId(UUID.fromString(rs.getString("productid")))

        val label = rs.getString("label")
        val size = VariantSize.lookup(rs.getString("size"))
        val color = VariantColor.lookup(rs.getString("color"))

        val quantityAvailable = QuantityAvailable(rs.getInt("quantityavailable"))

        return ProductVariant(
            sku = sku,
            productId = productId,
            label = label,
            size = size ?: VariantSize.NA, // Use a default value or handle as needed
            color = color ?: VariantColor.NA, // Use a default value or handle as needed
            quantityAvailable = quantityAvailable
        )
    }

}