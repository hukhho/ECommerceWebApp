package io.spring.shoestore.core.products

import java.math.BigDecimal

data class ProductLookupQuery(val byKeyword: String?, val byPrice: BigDecimal?) {
    fun isEmpty() = byKeyword == null && byPrice == null
}





