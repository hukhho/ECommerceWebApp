package io.spring.shoestore.app.http

import io.spring.shoestore.app.config.AuthenticationUtils
import io.spring.shoestore.core.cart.CartId
import io.spring.shoestore.core.cart.CartService
import io.spring.shoestore.core.cart.SessionId
import io.spring.shoestore.core.exceptions.QuantityExceedsAvailabilityException
import io.spring.shoestore.core.products.ProductService
import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.core.users.UserId
import io.spring.shoestore.core.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.math.BigDecimal



@Controller
class CartController(
    private val userService: UserService,
    private val productService: ProductService,
    private val cartService: CartService,
    private val storeAuthProvider: StoreAuthProvider,
    private val authenticationUtils: AuthenticationUtils,
) {
    val defaultUserId = UserId.from("00000000-0000-0000-0000-000000000000")
    val defaultSessionId = SessionId.from("00000000-0000-0000-0000-000000000000")

    @GetMapping("/cart")
    fun viewCart(authentication: Authentication?, model: Model): String {
        val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
        if (userDetails != null) {
            val userID = userDetails.getUserID()
            val sessionID = defaultSessionId

            val cartItems = cartService.getCartItems(sessionID, userID)
            val sortedCartItems = cartItems.sortedBy { it.sku }

            val grandTotal: BigDecimal = cartItems
                .map { item -> item?.product?.price?.multiply(BigDecimal(item.quantity)) ?: BigDecimal.ZERO }
                .fold(BigDecimal.ZERO) { acc, price -> acc.add(price) }

            model.addAttribute("grandTotal", grandTotal);
            model.addAttribute("cartItems", sortedCartItems)

            log.info("Cart items retrieved for user: $userID")
            return "cart"
        }
        return "login"
    }

    @PostMapping("/cart/add")
    fun addToCart(
        @RequestParam("sku") sku: String,
        @RequestParam("quantity") quantity: Int,
        authentication: Authentication?,
        redirectAttributes: RedirectAttributes
    ): String {
        val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
        if (userDetails != null) {
            val userID = userDetails.getUserID()
            val sessionID = defaultSessionId

            val isAddCartSuccess = cartService.addItemToCart(sessionID, userID, sku, quantity)

            if (!isAddCartSuccess) {
                // Add an error flash attribute
                redirectAttributes.addFlashAttribute("error", "Error adding item to cart")
            }

            log.info("get success user $userID ")
            log.info("Item added to cart for user: $userID")

            return "redirect:/cart" // Redirect to the "cart" page
        }
        return "login"
    }

    @PostMapping("/cart/update")
    fun updateCartItem(
        @RequestParam("id") id: String,
        @RequestParam("quantity") quantity: Int,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val cartId = CartId.from(id)
            if (quantity < 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Quantity must >0.")
                return "redirect:/cart"
            }
            val check = cartService.updateItemQuantity(cartId, quantity)

            if (check) {
                redirectAttributes.addFlashAttribute("successMessage", "Item updated successfully!")
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update item.")
            }
        } catch (ex: QuantityExceedsAvailabilityException) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.message)
        } catch (ex: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.message)
        }
        return "redirect:/cart"
    }

    @PostMapping("/cart/remove")
    fun removeCartItem(@RequestParam("id") id: String): String {
        val cartId = CartId.from(id)
        cartService.removeItemFromCart(cartId)
        return "redirect:/cart"
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderController::class.java)
    }

}
