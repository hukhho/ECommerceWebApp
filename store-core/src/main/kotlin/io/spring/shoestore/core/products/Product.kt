package io.spring.shoestore.core.products

import java.math.BigDecimal
import java.util.*

data class Product(
    val id: ProductId = ProductId(UUID.randomUUID()),
    val categoryID: CategoryId = CategoryId.from("00000000-0000-0000-0000-000000000000"),
    val categoryName: String = "",
    val productCode: String= "",
    val name: String= "",
    val description: String? = "",
    val imageURL: String? = "",
    val price: BigDecimal = BigDecimal.ZERO
)
data class Category(
    val id: CategoryId,
    val name: String
)
data class ProductId(val value: UUID) {
    companion object {
        @JvmStatic
        fun from(rawValue: String): ProductId {
            return ProductId(UUID.fromString(rawValue))
        }
    }
}
data class CategoryId(val value: UUID) {
    companion object {
        @JvmStatic
        fun from(rawValue: String): CategoryId {
            return CategoryId(UUID.fromString(rawValue))
        }
    }
}
