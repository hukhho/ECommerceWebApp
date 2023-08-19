package io.spring.shoestore.core.variants

import io.spring.shoestore.core.products.ProductId


class ProductVariant(val sku: Sku,
                     val productId: ProductId,
                     val label: String,
                     val size: VariantSize,
                     val color: VariantColor,
                     val quantityAvailable: QuantityAvailable) {
    override fun toString(): String {
        return "Variant: '$label' ($sku) $size, $color"
    }
}

data class Sku(val value: String) {
    init {
        assert(value.isNotEmpty())
        assert(value.length in 6..127)
    }
}

data class QuantityAvailable(val value: Int) {
    init {
        assert(value >= 0)
        assert(value <= Int.MAX_VALUE)
    }

    override fun toString(): String {
        return "quantity > 0 and < MAX_VALUE"
    }
}