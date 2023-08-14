package io.spring.shoestore.app.http

import io.spring.shoestore.app.http.api.UserData
import io.spring.shoestore.core.security.StoreAuthProvider
import io.spring.shoestore.core.users.User
import io.spring.shoestore.core.users.UserLookupQuery
import io.spring.shoestore.core.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UserController(private val userService: UserService,
                     private val storeAuthProvider: StoreAuthProvider) {
    @GetMapping("/users")
    fun listUsers(@RequestParam name: String?, model: Model): String {
        val query = UserLookupQuery(name)
        val users = userService.search(query)
        model.addAttribute("users", users)
        return "user-list"
    }

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

    private fun convertUser(domain: User): UserData = UserData(
        id = domain.id.value.toString(),
        username = domain.username,
        email = domain.email,
        fullName = domain.fullName,
        roleID = domain.roleID
    )

    companion object {
        private val log = LoggerFactory.getLogger(OrderController::class.java)
    }
}
