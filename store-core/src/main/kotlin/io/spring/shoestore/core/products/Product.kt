package io.spring.shoestore.core.products

import java.math.BigDecimal
import java.util.UUID

data class Product(
    val id: ProductId,
    val categoryID: UUID,
    val productCode: String,
    val name: String,
    val description: String?,
    val imageURL: String?,
    val price: BigDecimal
)

data class ProductId(val value: UUID) {

    companion object {
        @JvmStatic
        fun from(rawValue: String): ProductId {
            return ProductId(UUID.fromString(rawValue))
        }
    }
}

