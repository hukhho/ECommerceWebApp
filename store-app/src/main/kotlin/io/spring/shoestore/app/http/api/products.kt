package io.spring.shoestore.app.http.api

import java.math.BigDecimal


data class ProductResults(val products: List<ProductData>)

data class ProductData(
    val id: String,
    val categoryID: String,
    val categoryName: String,
    val productCode: String,
    val name: String,
    val description: String,
    val imageURL: String,
    val price: BigDecimal,
)

