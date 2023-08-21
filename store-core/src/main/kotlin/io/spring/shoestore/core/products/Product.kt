package io.spring.shoestore.core.products

import java.math.BigDecimal
import java.util.*

data class Product(
    var id: ProductId?,
    val categoryID: CategoryId,
    val categoryName: String = "",
    val productCode: String,
    val name: String,
    val description: String?,
    val imageURL: String?,
    val price: BigDecimal
) {
    fun validate(): List<Pair<String, String>> {
        val errors = mutableListOf<Pair<String, String>>()
        if (productCode == null || productCode.length <= 3) {
            errors.add(Pair("productCode", "Product Code must be more than 3 letters."))
        }
        return errors
    }
}
data class Category(
    val id: CategoryId,
    val name: String
)
data class ProductId(val value: UUID?) {
    companion object {
        private val DEFAULT_UUID = UUID.randomUUID()
        @JvmStatic
        fun from(rawValue: String?): ProductId {
            return try {
                ProductId(UUID.fromString(rawValue))
            } catch (e: Exception) {
                ProductId(DEFAULT_UUID)
            }
        }
    }
}
data class CategoryId(val value: UUID?) {
    companion object {
        private val DEFAULT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
        @JvmStatic
        fun from(rawValue: String?): CategoryId {
            return try {
                CategoryId(UUID.fromString(rawValue))
            } catch (e: Exception) {
                CategoryId(DEFAULT_UUID)
            }
        }
    }
}