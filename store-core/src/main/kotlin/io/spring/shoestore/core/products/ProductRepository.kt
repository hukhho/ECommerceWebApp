package io.spring.shoestore.core.products

import java.math.BigDecimal

interface ProductRepository {
    fun findById(id: ProductId): Product?

    fun list(): List<Product>

    fun findByKeyword(keywordPartial: String): List<Product>

    fun findByPriceUnder(upperPrice: BigDecimal): List<Product>
    fun create(product: Product): Boolean
    fun update(product: Product): Boolean
    fun delete(id: ProductId): Boolean
    fun restore(id: ProductId): Boolean
    fun listAllCategories(): List<Category>

}