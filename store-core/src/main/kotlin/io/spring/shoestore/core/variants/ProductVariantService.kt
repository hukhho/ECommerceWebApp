package io.spring.shoestore.core.variants

import io.spring.shoestore.core.products.ProductId
import org.slf4j.LoggerFactory

class ProductVariantService(
    private val productVariantRepository: ProductVariantRepository
) {
    private val log = LoggerFactory.getLogger(ProductVariantService::class.java)

    fun listForId(productId: ProductId): List<ProductVariant> {
        return productVariantRepository.findAllVariantsForProduct(productId)
    }

    fun findById(sku: String): ProductVariant? {
        return productVariantRepository.findById(sku)
    }

    fun addVariant(variant: ProductVariant): Boolean {
        try {
            productVariantRepository.registerNewVariants(listOf(variant))
            return true
        } catch (e: Exception) {
            log.error("Error while adding variant", e)
            return false
        }
    }

    fun updateVariant(variant: ProductVariant): Boolean {
        return try {
            productVariantRepository.updateVariant(variant)
            true
        } catch (e: Exception) {
            log.error("Error while updating variant", e)
            false
        }
    }

    fun deleteVariant(sku: String): Boolean {
        return try {
            productVariantRepository.deleteVariant(sku)
            true
        } catch (e: Exception) {
            log.error("Error while deleting variant", e)
            false
        }
    }

}