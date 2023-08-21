package io.spring.shoestore.app.http

import io.spring.shoestore.app.http.api.ProductData
import io.spring.shoestore.core.products.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.math.BigDecimal

@Controller
@RequestMapping("/admin")
class AdminProductController(
    private val productService: ProductService
) {
    @GetMapping("/products")
    fun listProducts(model: Model): String {
        val products = productService.search(ProductLookupQuery("", null)) // Fetch all products
        model.addAttribute("products", products)

        return "admin/admin-product-list"
    }
    @GetMapping("/product/create")
    fun showCreateProductForm(model: Model): String {
        val categories = productService.getAllCategories()
        model.addAttribute("categories", categories)
        model.addAttribute("product", Product(
            ProductId(null),
            CategoryId.from(null),
            "",
            "",
            "",
            "",
            "",
            BigDecimal.ZERO
        ))
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
                return "redirect:/admin/products"
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

    @GetMapping("/product/{productId}/edit")
    fun showEditProductForm(@PathVariable productId: String,
                            model: Model
    ): String {

        val product = productService.get(ProductId.from(productId))
        val categories = productService.getAllCategories()

        model.addAttribute("product", product)
        model.addAttribute("categories", categories)

        return "product-edit"
    }

    @PostMapping("/product/{productId}/edit")
    fun updateProduct(
        @PathVariable productId: String,
        @ModelAttribute product: Product,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        val productErrors = product.validate()

        for (error in productErrors) {
            bindingResult.rejectValue(error.first, "error.product", error.second)
        }

        if (bindingResult.hasErrors()) {
            log.error("Validation errors: ${bindingResult.allErrors}");
            val product = productService.get(ProductId.from(productId))
            val categories = productService.getAllCategories()

            model.addAttribute("categories", categories)
            model.addAttribute("product", product);
            model.addAttribute("org.springframework.validation.BindingResult.product", bindingResult);

            return "product-edit";
        }

        product.id = ProductId.from(productId)

        if (productService.update(product)) {
            log.info("Product updated successfully: ${product.name}")
            return "redirect:/admin/products";
        } else {
            log.error("Failed to update product:${product.id?.value} ${product.name}")
            redirectAttributes.addFlashAttribute("error", "Failed to update product")
            return "redirect:/admin/product/$productId/edit";
        }
    }

    @GetMapping("/product/{productId}/delete")
    fun deleteProduct(@PathVariable productId: String): String {
        val productIdObj = ProductId.from(productId)
        if (productService.delete(productIdObj)) {
            log.info("Product deleted successfully: $productId")
            return "redirect:/admin/products"
        } else {
            log.error("Failed to delete product: $productId")
            return "product-error" // Assuming you have a view for errors
        }
    }

    @GetMapping("/product/{productId}/restore")
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
        private val log = LoggerFactory.getLogger(AdminProductController::class.java)
    }
}
