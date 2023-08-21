package io.spring.shoestore.app.http

import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.core.users.User
import io.spring.shoestore.core.users.UserCreate
import io.spring.shoestore.core.users.UserId
import io.spring.shoestore.core.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
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

    @GetMapping("/user/create")
    fun createUserForm(model: Model): String {
        model.addAttribute("user",
            UserCreate("",
                "",
                "",
                "",
                "",
                roleID = "US",
            )
        )
        return "user-create"
    }

    @PostMapping("/user/create")
    fun createUser(@ModelAttribute user: UserCreate, model: Model): String {
        val newUserId = UserId.from(UUID.randomUUID().toString())

        val newUser = User(
            newUserId,
            user.username,
            user.email,
            user.fullName,
            user.password,
            user.roleID,
            true)

        userService.save(newUser)

        return "redirect:/login"
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderController::class.java)
    }
}
