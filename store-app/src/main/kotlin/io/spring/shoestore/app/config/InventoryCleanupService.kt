package io.spring.shoestore.app.config

import io.spring.shoestore.core.cart.CartService
import io.spring.shoestore.core.inventory.InventoryWarehousingRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class InventoryCleanupService(
    private val cartService: CartService,
    private val inventoryWarehousingRepository: InventoryWarehousingRepository
) {
    @Scheduled(fixedRate = 60000) // Run every minute
    fun cleanupExpiredReservations() {



    }
}
