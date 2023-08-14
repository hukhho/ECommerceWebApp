package io.spring.shoestore.core.products

import java.math.BigDecimal

data class ProductLookupQuery(val byName: String?, val byPrice: BigDecimal?) {
    fun isEmpty() = byName == null && byPrice == null
}





