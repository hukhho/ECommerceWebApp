package io.spring.shoestore.app.http

import io.spring.shoestore.app.http.api.ProductData
import io.spring.shoestore.app.http.api.ProductResults
import io.spring.shoestore.core.products.Product
import io.spring.shoestore.core.products.ProductLookupQuery
import io.spring.shoestore.core.products.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Controller
class LoginController(private val productService: ProductService) {


    @Controller
    class LoginController {

        @GetMapping("/login")
        fun showLoginForm(): String {
            return "login"
        }

        @PostMapping("/login")
        fun processLogin(username: String, password: String): String {
            // Check the provided username and password (you can use your authentication logic here)
            // For simplicity, we'll assume a hardcoded username and password
            if (username == "user" && password == "password") {
                return "redirect:/products"
            } else {
                return "redirect:/login?error=true"
            }
        }
    }


}