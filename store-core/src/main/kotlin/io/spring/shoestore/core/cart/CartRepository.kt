package io.spring.shoestore.core.cart

import io.spring.shoestore.core.users.UserId

interface CartRepository {
    fun addItem(cartItem: Cart): Boolean
    fun clearCart(sessionID: SessionId?, userID: UserId?): Boolean
    fun updateItem(cartItem: Cart)
    fun removeItem(id: CartId)
    fun getCartItems(sessionID: SessionId?, userID: UserId?): List<Cart>
    fun bulkUpdateItems(updatedItems: List<Cart>): Boolean
    fun updateItemQuantity(cartId: CartId, quantity: Int): Boolean
}
