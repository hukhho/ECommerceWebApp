package io.spring.shoestore.app.http

import io.spring.shoestore.app.http.api.ProductData
import io.spring.shoestore.core.inventory.InventoryWarehousingRepository
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.products.ProductId
import io.spring.shoestore.core.products.ProductLookupQuery
import io.spring.shoestore.core.products.ProductService
import io.spring.shoestore.core.variants.ProductVariant
import io.spring.shoestore.core.variants.ProductVariantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class ProductController(
    private val productService: ProductService,
    private val productVariantService: ProductVariantService,
    private val inventoryRepository: InventoryWarehousingRepository
) {
    @GetMapping("/products")
    fun listProducts(
        @RequestParam keyword: String?,
        model: Model
    ): String {
        val query = ProductLookupQuery(keyword, null)
        val products = productService.search(query).map { convert(it) }
        model.addAttribute("products", products)
        return "product-list"
    }

    @GetMapping("/product/{productId}")
    fun getProductVariants(
        @PathVariable productId: String,
        model: Model
    ): String {
        val productIdObj = ProductId.from(productId)
        val listVariant = productVariantService.listForId(productIdObj)

        log.info("Fetching variants for productId $productId")

        // Fetch real-time quantity from Redis for each variant
        val realTimeVariants = listVariant.map { variant ->
            val realTimeQuantity = inventoryRepository.getAvailableInventory(variant.sku)
            ProductVariant(
                sku = variant.sku,
                productId = variant.productId,
                label = variant.label,
                size = variant.size,
                color = variant.color,
                quantityAvailable = realTimeQuantity
            )
        }
        model.addAttribute("product", productService.get(productIdObj))
        model.addAttribute("listVariant", realTimeVariants)

        return "product-variants"
    }

    private fun convert(domain: Product): ProductData = ProductData(
        id = domain.id?.value.toString(),
        categoryID = domain.categoryID.value.toString(),
        categoryName = domain.categoryName,
        productCode = domain.productCode,
        name = domain.name,
        description = domain.description ?: "",
        imageURL = domain.imageURL ?: "",
        price = domain.price,
    )

    companion object {
        private val log = LoggerFactory.getLogger(OrderController::class.java)
    }
}