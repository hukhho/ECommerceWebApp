package io.spring.shoestore.redis

import io.spring.shoestore.core.inventory.InventoryWarehousingRepository
import io.spring.shoestore.core.orders.OrderRepository
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis

class RedisInventoryWarehousingRepository(
    private val redisClient: Jedis,
    private val orderRepository: OrderRepository // This is a hypothetical interface to fetch initial stock from the database
) : InventoryWarehousingRepository {

    private val reservationPrefix = "reservation:"
    private val logger = LoggerFactory.getLogger(RedisInventoryWarehousingRepository::class.java)
    // Save the association between order ID and SKU + quantity
    override fun saveOrderReservation(orderId: String, sku: String, quantity: Int) {
        val orderKey = "order:$orderId"
        redisClient.hset(orderKey, sku, quantity.toString())
        redisClient.expire(orderKey, 1 * 10 * 60) // 10-mins expiration

        logger.info("Saved reservation for Order ID: $orderId, SKU: $sku, Quantity: $quantity")
    }

    // Retrieve the SKUs and quantities associated with an order ID
    override fun getOrderReservations(orderId: String): Map<String, String> {
        val orderKey = "order:$orderId"
        val reservations = redisClient.hgetAll(orderKey)

        logger.info("Retrieved reservations for Order ID: $orderId -> $reservations")

        return reservations
    }


    override fun reserveInventory(sku: String, quantity: Int): Boolean {
            val available = getAvailableInventory(sku)
        if (available < quantity) {
            logger.warn("Failed to reserve inventory. SKU: $sku, Requested: $quantity, Available: $available")
            return false
        }
        val key = reservationPrefix + sku
        redisClient.decrBy(key, quantity.toLong())
        redisClient.expire(key, 20 * 60) // Set a 20-mins expiration
        logger.info("Reserved inventory. SKU: $sku, Quantity: $quantity")
        return true
    }

    override fun releaseInventory(sku: String, quantity: Int): Boolean {
        redisClient.incrBy(reservationPrefix + sku, quantity.toLong())
        logger.info("Released inventory. SKU: $sku, Quantity: $quantity")
        return true
    }

    override fun getAvailableInventory(sku: String): Int {
        val key = reservationPrefix + sku
            var available = redisClient.get(key)?.toInt()

        // If the value isn't in Redis, fetch from the database and set in Redis
        if (available == null) {
            available = orderRepository.getCurrentInventory(sku) // Hypothetical method to fetch stock from the database
            redisClient.set(key, available.toString())
        }

        logger.debug("Fetched available inventory. SKU: $sku, Available: $available")
        return available
    }

    override fun deleteOrderReservations(orderId: String) {
        val orderKey = "order:$orderId"
        redisClient.del(orderKey)
        logger.info("Deleted reservations for Order ID: $orderId")
    }


//    override fun cleanupExpiredReservations(): Int {
//        var count = 0
//        var cursor = "0"
//        do {
//            val scanResult = redisClient.scan(cursor, "MATCH", "$reservationPrefix*")
//            cursor = scanResult.stringCursor
//            for (key in scanResult.result) {
//                if (redisClient.ttl(key) == -2L) {
//                    redisClient.del(key)
//                    count++
//                }
//            }
//        } while (cursor != "0")
//        logger.info("Cleaned up $count expired reservations.")
//        return count
//    }

}
