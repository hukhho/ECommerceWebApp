package io.spring.shoestore.app.http

import io.spring.shoestore.app.http.api.ProductData
import io.spring.shoestore.app.http.api.ProductResults
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.products.ProductLookupQuery
import io.spring.shoestore.core.products.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Controller
class ProductController(private val productService: ProductService) {

    @GetMapping("/products")
    fun listProducts(@RequestParam name: String?, model: Model): String {
        val query = ProductLookupQuery(name, null)
        val products = productService.search(query).map { convert(it) }
        model.addAttribute("products", products)
        return "product-list"
    }

    private fun convert(domain: Product): ProductData = ProductData(
        id = domain.id.value.toString(),
        categoryID = domain.categoryID,
        productCode = domain.productCode,
        name = domain.name,
        description = domain.description ?: "",
        imageURL = domain.imageURL ?: "",
        price = domain.price,
    )
}