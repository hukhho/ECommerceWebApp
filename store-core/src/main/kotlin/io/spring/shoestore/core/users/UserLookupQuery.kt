package io.spring.shoestore.core.users

import java.math.BigDecimal

data class UserLookupQuery(val byName: String?) {
    fun isEmpty() = byName == null
}





