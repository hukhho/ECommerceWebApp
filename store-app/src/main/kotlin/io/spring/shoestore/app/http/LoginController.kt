package io.spring.shoestore.app.http

import io.spring.shoestore.core.products.ProductService
import org.springframework.stereotype.Controller

@Controller
class LoginController(private val productService: ProductService) {

    @Controller
    class LoginController {

//        @GetMapping("/login")
//        fun showLoginForm(): String {
//            return "login"
//        }

//        @PostMapping("/login")
//        fun processLogin(username: String, password: String): String {
//            if (username == "user" && password == "password") {
//                return "redirect:/products"
//            } else {
//                return "redirect:/login?error=true"
//            }
//        }
    }


}