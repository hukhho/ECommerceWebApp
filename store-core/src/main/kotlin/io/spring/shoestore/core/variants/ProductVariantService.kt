package io.spring.shoestore.core.variants

import io.spring.shoestore.core.products.ProductId

class ProductVariantService(
    private val productVariantRepository: ProductVariantRepository
) {
    fun listForId(productId: ProductId): List<ProductVariant> {
        return productVariantRepository.findAllVariantsForProduct(productId)
    }
}