
import io.spring.shoestore.core.cart.Cart
import io.spring.shoestore.core.cart.CartId
import io.spring.shoestore.core.cart.CartRepository
import io.spring.shoestore.core.cart.SessionId
import io.spring.shoestore.core.exceptions.QuantityExceedsAvailabilityException
import io.spring.shoestore.core.users.UserId
import io.spring.shoestore.postgres.mappers.CartMapper
import org.springframework.jdbc.core.JdbcTemplate

class PostgresCartRepository(
    private val jdbcTemplate: JdbcTemplate
) : CartRepository {

    private val cartMapper = CartMapper()

    private val defaultUserId = UserId.from("00000000-0000-0000-0000-000000000000")
    private val defaultSessionId = SessionId.from("00000000-0000-0000-0000-000000000000")
    override fun addItem(cartItem: Cart): Boolean {
        val rowsAffected = jdbcTemplate.update(
            "INSERT INTO tbl_Cart (sessionID, userID, sku, quantity)" +
                    "VALUES (?, ?, ?, ?)" +
                    "ON CONFLICT (sessionID, userID, sku) DO " +
                    "UPDATE SET quantity = tbl_Cart.quantity + EXCLUDED.quantity;",
            cartItem.sessionID?.value,
            cartItem.userID?.value,
            cartItem.sku,
            cartItem.quantity
        )
        return rowsAffected > 0
    }

    override fun clearCart(sessionID: SessionId?, userID: UserId?): Boolean {
        return try {
            if (userID != null) {
                jdbcTemplate.update(
                    "DELETE FROM tbl_Cart WHERE sessionID = ? AND userID = ?",
                    sessionID?.value ?: SessionId.from(null).value,
                    userID?.value ?: UserId.from(null).value,
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun bulkUpdateItems(updatedItems: List<Cart>): Boolean {
        val batchArgs: List<Array<Any?>> = updatedItems.map { item ->
            arrayOf(
                item.sessionID?.value ?: defaultSessionId,
                item.userID?.value ?: defaultUserId,
                item.sku,
                item.quantity
            )
        }

        val updateCounts = jdbcTemplate.batchUpdate(
            "INSERT INTO tbl_Cart (sessionID, userID, sku, quantity) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (sessionID, userID, sku) " +
                    "DO UPDATE SET quantity = EXCLUDED.quantity;",
            batchArgs
        )

        // Check if all update counts are greater than or equal to zero (success)
        return updateCounts.all { it >= 0 }
    }

    override fun updateItem(cartItem: Cart) {
        jdbcTemplate.update(
            "UPDATE tbl_Cart SET sessionID = ?, userID = ?, sku = ?, quantity = ? WHERE id = ?;",
            cartItem.sessionID, cartItem.userID?.value, cartItem.sku, cartItem.quantity, cartItem.id
        )
    }

    override fun updateItemQuantity(cartId: CartId, quantity: Int): Boolean {
        // Fetch available quantity for the product
        val availableQuantity = jdbcTemplate.queryForObject(
            "SELECT pv.quantityAvailable FROM tbl_Cart c " +
                    "JOIN tbl_Product_Variation pv ON c.sku = pv.sku " +
                    "WHERE c.id = ?;",
            Int::class.java,
            cartId.value
        ) ?: 0

        // Check if requested quantity exceeds available quantity
        if (quantity > availableQuantity) {
            throw QuantityExceedsAvailabilityException("Requested quantity exceeds available stock.")
        }

        val rowsAffected = jdbcTemplate.update(
            "UPDATE tbl_Cart SET quantity = ? WHERE id = ?;",
            quantity, cartId.value
        )
        return rowsAffected > 0
    }


    override fun removeItem(id: CartId) {
        jdbcTemplate.update(
            "DELETE FROM tbl_Cart WHERE id = ?;",
            id.value
        )
    }

    override fun getCartItems(sessionID: SessionId?, userID: UserId?): List<Cart> {
        val carts = jdbcTemplate.query(
            "SELECT c.*, pv.label, pv.size, pv.color, pv.quantityAvailable, p.*, cat.name " +
                    "AS categoryName FROM tbl_Cart c " +
                    "JOIN tbl_Product_Variation pv ON c.sku = pv.sku " +
                    "JOIN tbl_Product p ON pv.productID = p.id " +
                    "JOIN tbl_Category cat ON p.categoryID = cat.id " +
                    "WHERE c.sessionID = ? AND c.userID = ?;",
            cartMapper,
            sessionID?.value ?: defaultUserId.value,
            userID?.value ?: defaultSessionId.value
        )
        return carts;
    }





}
