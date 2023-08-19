package io.spring.shoestore.core.variants

import io.spring.shoestore.core.products.ProductId

interface ProductVariantRepository  {
    fun findAllVariantsForProduct(productId: ProductId): List<ProductVariant>

    fun findById(sku: Sku): ProductVariant?

    fun registerNewVariants(variants: List<ProductVariant>)
}