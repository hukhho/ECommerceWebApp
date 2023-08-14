package io.spring.shoestore.app.http

import io.spring.shoestore.app.http.api.ProductData
import io.spring.shoestore.app.http.api.ProductResults
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.products.ProductLookupQuery
import io.spring.shoestore.core.products.ProductService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


/**
 * @author Eleftheria Stein
 */
@Controller
class MainController {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/user/index")
    fun userIndex(): String {
        return "user/index"
    }
    @GetMapping("/admin/index")
    fun adminIndex(): String {
        return "admin/index"
    }
    @GetMapping("/log-in")
    fun login(): String {
        return "login"
    }
}