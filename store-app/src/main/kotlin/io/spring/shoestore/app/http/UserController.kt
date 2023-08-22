package io.spring.shoestore.app.http

import io.spring.shoestore.core.exceptions.ServiceException
import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.core.users.*
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

@Controller
class UserController(private val userService: UserService,
                     private val storeAuthProvider: StoreAuthProvider) {
    @GetMapping("/my-profile")
    fun myProfile(authentication: Authentication?, model: Model): String {
        if (authentication == null) {
            return "redirect:/login"  // Redirect to the login page
        }
        log.info("Fetching orders for user ${storeAuthProvider.getCurrentUser()}")

        val username = authentication.name
        val roles = authentication.authorities.map { it.authority }

        model.addAttribute("username", username)
        model.addAttribute("roles", roles)

        return "my-profile"
    }

    @GetMapping("/register")
    fun createUserForm(
        model: Model,
    ): String {
        model.addAttribute(
            "user",
            UserCreate(
                "",
                "",
                "",
                "",
                "",
                roleID = "US"
            )
        )
        return "register"
    }

    @PostMapping("/register")
    fun createUser(
        @ModelAttribute user: UserCreate,
        model: Model,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val userCreateErrors = UserCreateValidator().validate(user)
            for (error in userCreateErrors) {
                bindingResult.rejectValue(error.first, "error.user", error.second)
            }
            // Check for duplicate username
            if (user.roleID != "US") {
                bindingResult.rejectValue("username", "error.user", "User only create account with US role.")
            }
            // Check for duplicate username
            if (userService.findByUsername(user.username) != null) {
                bindingResult.rejectValue("username", "error.user", "Username already exists.")
            }
            // Check for duplicate email
            if (userService.findByEmail(user.email) != null) {
                bindingResult.rejectValue("email", "error.user", "Email already exists.")
            }
            if (bindingResult.hasErrors()) {
                log.error("Validation errors: ${bindingResult.allErrors}")
                model.addAttribute("org.springframework.validation.BindingResult.user", bindingResult)

                return "register"
            }

            val newUserId = UserId.from(UUID.randomUUID().toString())

            val passwordEncoder = BCryptPasswordEncoder()
            val hashedPassword = passwordEncoder.encode(user.password)

            val newUser = User(
                newUserId,
                user.username,
                user.email,
                user.fullName,
                hashedPassword,
                "US",
                true
            )
            userService.save(newUser)
        } catch (e: ServiceException) {
            log.error("Caught ServiceException during user creation: ${e.message}", e)
            log.error("Error during user creation: ${e.message}", e)
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "An error occurred while creating the user. Please try again. Error ${e.message}"
            )
            return "redirect:/register"
        } catch (e: Exception) {
            log.error("Caught general exception during user creation: ${e.message}", e)
            log.error("Error during user creation: ${e.message}", e)
            redirectAttributes.addFlashAttribute("errorMessage",
                "An error occurred while creating the user. Please try again.")

            return "redirect:/error"
        }

        return "redirect:/login?registerSuccess=true"
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderController::class.java)
    }
}
