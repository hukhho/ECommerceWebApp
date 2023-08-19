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

    override fun reserveInventory(sku: String, quantity: Int): Boolean {
        val available = getAvailableInventory(sku)
        if (available < quantity) {
            logger.warn("Failed to reserve inventory. SKU: $sku, Requested: $quantity, Available: $available")
            return false
        }
        val key = reservationPrefix + sku
        redisClient.decrBy(key, quantity.toLong())
        redisClient.expire(key, 1 * 15) // Set a 15-minute expiration
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
