package io.spring.shoestore.app.http

import io.spring.shoestore.app.http.api.ProductData
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.products.ProductId
import io.spring.shoestore.core.products.ProductLookupQuery
import io.spring.shoestore.core.products.ProductService
import io.spring.shoestore.core.variants.ProductVariantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class ProductController(
    private val productService: ProductService,
    private val productVariantService: ProductVariantService
) {
    @GetMapping("/products")
    fun listProducts(
        @RequestParam keyword: String?,
        model: Model
    ): String {
        val query = ProductLookupQuery(keyword, null)
        val products = productService.search(query).map { convert(it) }
        model.addAttribute("products", products)
        return "product-list" //still not have show all products in table for admin, this is show as card for public
    }

    @GetMapping("/product/{productId}")
    fun getProductVariants(
        @PathVariable productId: String,
        model: Model
    ): String {
        val productIdObj = ProductId.from(productId)
        val listVariant = productVariantService.listForId(productIdObj)

        log.info("Fetching variants for productId $productId")

        model.addAttribute("product", productService.get(productIdObj)) // Add the product to the model
        model.addAttribute("listVariant", listVariant)

        return "product-variants"
    }

    @GetMapping("/product/create")
    fun showCreateProductForm(model: Model): String {
        val categories = productService.getAllCategories()
        model.addAttribute("categories", categories)
        model.addAttribute("product", Product()) // Initialize an empty product for the form

        return "product-create"
    }

    @PostMapping("/product")
    fun createProduct(@ModelAttribute product: Product, model: Model): String {
        try {
            val categories = productService.getAllCategories()
            model.addAttribute("categories", categories)

            log.info("product: ${product}")
            if (productService.create(product)) {
                log.info("Product created successfully: ${product.name}")
                return "redirect:/products"
            } else {
                log.error("Failed to create product: ${product.name}")
                model.addAttribute("error", "Failed to create product")
                return "product-create"
            }
        } catch (e: Exception) {
            log.error("Error while creating product", e)
            model.addAttribute("error", e.message)
            return "product-error"
        }
    }


    @PutMapping("/product/{productId}")
    fun updateProduct(
        @PathVariable productId: String,
        @ModelAttribute product: Product,
        model: Model
    ): String {
        if (productService.update(product)) {
            log.info("Product updated successfully: ${product.name}")
            return "redirect:/product/$productId"
        } else {
            log.error("Failed to update product: ${product.name}")
            model.addAttribute("error", "Failed to update product")
            return "product-edit" // Assuming you have a view for product editing
        }
    }

    @DeleteMapping("/product/{productId}")
    fun deleteProduct(@PathVariable productId: String): String {
        val productIdObj = ProductId.from(productId)
        if (productService.delete(productIdObj)) {
            log.info("Product deleted successfully: $productId")
            return "redirect:/products"
        } else {
            log.error("Failed to delete product: $productId")
            return "product-error" // Assuming you have a view for errors
        }
    }

    @PostMapping("/product/{productId}/restore")
    fun restoreProduct(@PathVariable productId: String): String {
        val productIdObj = ProductId.from(productId)
        if (productService.restore(productIdObj)) {
            log.info("Product restored successfully: $productId")
            return "redirect:/product/$productId"
        } else {
            log.error("Failed to restore product: $productId")
            return "product-error" // Assuming you have a view for errors
        }
    }

    private fun convert(domain: Product): ProductData = ProductData(
        id = domain.id.value.toString(),
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