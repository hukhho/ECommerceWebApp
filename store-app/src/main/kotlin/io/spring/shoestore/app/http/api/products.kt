package io.spring.shoestore.app.http.api

import java.math.BigDecimal
import java.util.*


data class ProductResults(val products: List<ProductData>)

data class ProductData(
    val id: String,
    val categoryID: UUID,
    val productCode: String,
    val name: String,
    val description: String,
    val imageURL: String,
    val price: BigDecimal,
)