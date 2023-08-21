package io.spring.shoestore.app.http

import io.spring.shoestore.core.exceptions.ServiceException
import io.spring.shoestore.core.users.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

@Controller
@RequestMapping("/admin")
class AdminUserController(
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/users")
    fun listUsers(@RequestParam name: String?, model: Model): String {
        val query = UserLookupQuery(name)
        val users = userService.search(query)
        model.addAttribute("users", users)
        return "admin/admin-user-list"
    }

    @GetMapping("/user/create")
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
        return "user-create"
    }

    @PostMapping("/user/create")
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
            if (bindingResult.hasErrors()) {
                log.error("Validation errors: ${bindingResult.allErrors}")
                model.addAttribute("org.springframework.validation.BindingResult.user", bindingResult)
                return "user-create"
            }
            val newUserId = UserId.from(UUID.randomUUID().toString())
            val newUser = User(
                newUserId,
                user.username,
                user.email,
                user.fullName,
                user.password,
                user.roleID,
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
            return "redirect:/admin/user/create"
        } catch (e: Exception) {
            log.error("Caught general exception during user creation: ${e.message}", e)
            log.error("Error during user creation: ${e.message}", e)
            redirectAttributes.addFlashAttribute("errorMessage",
                "An error occurred while creating the user. Please try again.")
            return "error"
        }

        return "redirect:/admin/users"
    }


    @GetMapping("/user/{id}/edit")
    fun editUserForm(@PathVariable id: String, model: Model): String {
        val user = userService.get(UserId.from(id))

        if (user == null) {
            model.addAttribute("errorMessage", "Not found user with id: ${id}")
            return "error"
        }
        val userUpdate = convertUserToUserUpdate(user)

        model.addAttribute("user", userUpdate)

        return "user-edit"
    }

    @PostMapping("/user/{id}/edit")
    fun editUser(@PathVariable id: String, @ModelAttribute user: UserUpdate): String {
        userService.update(user)
        return "redirect:/admin/users"
    }

    // Delete User
    @GetMapping("/user/{id}/delete")
    fun deleteUser(@PathVariable id: String): String {
        userService.deleteById(UserId.from(id))
        return "redirect:/admin/users"
    }

    private fun convertUserToUserUpdate(domain: User): UserUpdate = UserUpdate(
        id = domain.id,
        username = domain.username,
        email = domain.email,
        fullName = domain.fullName,
        roleID = domain.roleID,
        status = domain.status
    )


}
