package io.spring.shoestore.core.variants

import io.spring.shoestore.core.products.ProductId

class ProductVariant(val sku: String,
                     val productId: ProductId,
                     val label: String,
                     val size: VariantSize,
                     val color: VariantColor,
                     val quantityAvailable: Int
) {
    override fun toString(): String {
        return "Variant: '$label' ($sku) $size, $color"
    }
}

