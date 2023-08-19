package io.spring.shoestore.app.http

import io.spring.shoestore.app.exception.AppException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


/**
 * @author Eleftheria Stein
 */
@Controller
class MainController {

    @GetMapping("/")
    fun index(): String {
        return "redirect:/products"
    }
        @GetMapping("/test-error")
        fun testError(): String {
            throw AppException("Test ne hihi!")
            return "redirect:/products"
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