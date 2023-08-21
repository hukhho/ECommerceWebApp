package io.spring.shoestore.app.http

import io.spring.shoestore.core.products.ProductId
import io.spring.shoestore.core.variants.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/product/{productId}/variants")
class AdminProductVariantController(
    private val productVariantService: ProductVariantService
) {
    private val log = LoggerFactory.getLogger(AdminProductVariantController::class.java)

    @GetMapping
    fun listVariants(@PathVariable productId: String, model: Model): String {
        val variants = productVariantService.listForId(ProductId.from(productId))
        model.addAttribute("variants", variants)
        return "admin/product-variants-list"
    }

    @GetMapping("/create")
    fun showCreateVariantForm(@PathVariable productId: String, model: Model): String {
        model.addAttribute("productId", productId)
        model.addAttribute("variant",
            ProductVariant(
                (""),
                ProductId.from(productId),
                "",
                VariantSize.NA,
                VariantColor.NA,
                0
            ))
        return "admin/product-variant-create"
    }

    @PostMapping("/create")
    fun createVariant(@PathVariable productId: String,
                      @ModelAttribute variant: ProductVariant,
                      model: Model): String {
        try {
            productVariantService.addVariant(variant)
            return "redirect:/admin/product/$productId/variants"
        } catch (e: Exception) {
            log.error("Error while creating variant", e)
            model.addAttribute("error", e.message)
            return "redirect:/admin/product/$productId/variants"
        }
    }

    @GetMapping("/{sku}/edit")
    fun showEditVariantForm(@PathVariable productId: String, @PathVariable sku: String, model: Model): String {
        val variant = productVariantService.findById(sku)
        model.addAttribute("variant", variant)
        return "admin/product-variant-edit"
    }

    @PostMapping("/{sku}/edit")
    fun updateVariant(@PathVariable productId: String,
                      @PathVariable sku: String,
                      @ModelAttribute variant: ProductVariant,
                      model: Model): String {
        try {
            productVariantService.updateVariant(variant)
            return "redirect:/admin/product/$productId/variants"
        } catch (e: Exception) {
            log.error("Error while updating variant", e)
            model.addAttribute("error", e.message)
            return "redirect:/admin/product/$productId/variants"
        }
    }

    @GetMapping("/{sku}/delete")
    fun deleteVariant(@PathVariable productId: String, @PathVariable sku: String): String {
        productVariantService.deleteVariant(sku)
        return "redirect:/admin/product/$productId/variants"
    }
}
