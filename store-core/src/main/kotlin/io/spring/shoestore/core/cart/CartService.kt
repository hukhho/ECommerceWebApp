package io.spring.shoestore.core.cart

import io.spring.shoestore.core.users.UserId
import java.util.*

class CartService(private val repository: CartRepository) {
    fun addItemToCart(sessionID: SessionId?, userID: UserId?, sku: String, quantity: Int): Boolean {
        val cartItem = Cart(
            id = CartId.from(UUID.randomUUID().toString()),
            sessionID = sessionID,
            userID = userID,
            sku = sku,
            product = null,
            label = null,
            size = null,
            color =  null,
            quantity = quantity,
            quantityAvailable = 0
        )
        return repository.addItem(cartItem)
    }
    fun bulkUpdateCartItems(updatedItems: List<Cart>): Boolean {
        return repository.bulkUpdateItems(updatedItems)
    }
    fun updateCartItem(cartItem: Cart) {
        repository.updateItem(cartItem)
    }
    fun updateItemQuantity(cartId: CartId, quantity: Int): Boolean {
        return repository.updateItemQuantity(cartId, quantity)
    }
    fun removeItemFromCart(id: CartId) {
        repository.removeItem(id)
    }
    fun clearCart(sessionID: SessionId?, userID: UserId?) {
        repository.clearCart(sessionID, userID);
    }
    fun getCartItems(sessionID: SessionId?, userID: UserId?): List<Cart> {
        return repository.getCartItems(sessionID, userID)
    }
}



