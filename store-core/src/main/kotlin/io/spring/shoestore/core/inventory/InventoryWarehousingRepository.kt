package io.spring.shoestore.core.inventory

interface InventoryWarehousingRepository {
    fun reserveInventory(sku: String, quantity: Int): Boolean
    fun releaseInventory(sku: String, quantity: Int): Boolean
    fun getAvailableInventory(sku: String): Int
    fun saveOrderReservation(orderId: String, sku: String, quantity: Int)
    fun getOrderReservations(orderId: String): Map<String, String>
}
