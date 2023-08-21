package io.spring.shoestore.core.variants

import io.spring.shoestore.core.products.ProductId

interface ProductVariantRepository  {
    fun findAllVariantsForProduct(productId: ProductId): List<ProductVariant>
    fun findById(sku: String): ProductVariant?
    fun registerNewVariants(variants: List<ProductVariant>): Boolean
    fun updateVariant(variant: ProductVariant): Boolean
    fun deleteVariant(sku: String): Boolean
}