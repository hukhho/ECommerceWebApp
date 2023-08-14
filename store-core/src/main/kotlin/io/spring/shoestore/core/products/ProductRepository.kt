package io.spring.shoestore.core.products

import java.math.BigDecimal

interface ProductRepository {
    fun findById(id: ProductId): Product?

    fun list(): List<Product>

    fun findByName(namePartial: String): List<Product>

    fun findByPriceUnder(upperPrice: BigDecimal): List<Product>
}