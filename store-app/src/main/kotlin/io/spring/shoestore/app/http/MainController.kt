package io.spring.shoestore.app.http

import io.spring.shoestore.app.config.AuthenticationUtils
import io.spring.shoestore.app.exception.AppException
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


/**
 * @author hukhho
 */
@Controller
class MainController(
    private val authenticationUtils: AuthenticationUtils,
) {
    @GetMapping("/")
    fun index(authentication: Authentication?, model: Model): String {
        try {
            val userDetails = authenticationUtils.getUserDetailsFromAuthentication(authentication)
            if (userDetails != null) {
                model.addAttribute("userDetails", userDetails)
                log.info("User email: ${userDetails.getEmail()}")
                if (userDetails.authorities.any { it.authority == AUTHORITY_ADMIN }) {
                    return "redirect:/admin/products"
                }
            }
        }catch (e: Exception) {
            log.info(e.message)
        }
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
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }
    companion object {
        const val AUTHORITY_ADMIN = "AD"

        private val log = LoggerFactory.getLogger(MainController::class.java)
    }
}