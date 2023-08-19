package io.spring.shoestore.core.cart

data class CartLookupQuery(val byName: String?) {
    fun isEmpty() = byName == null
}





