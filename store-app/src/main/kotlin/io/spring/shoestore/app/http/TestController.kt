package io.spring.shoestore.core.users

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

class PersonForm {
    var name: String? = null
    var age: Int? = null

    fun validate(): List<Pair<String, String>> {
        val errors = mutableListOf<Pair<String, String>>()

        name?.let {
            if (it.length !in 2..30) {
                errors.add(Pair("name", "Name must be between 2 and 30 characters."))
            }
        } ?: errors.add(Pair("name", "Name must not be null."))

        age?.let {
            if (it < 18) {
                errors.add(Pair("age", "Age must be at least 18."))
            }
        } ?: errors.add(Pair("age", "Age must not be null."))

        return errors
    }

    override fun toString(): String {
        return "Person(Name: $name, Age: $age)"
    }
}

@Controller
class TestController {

    private val log = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("/test123")
    fun showForm(personForm: PersonForm?): String {
        return "form"
    }

    @PostMapping("/test123")
    fun checkPersonInfo(personForm: PersonForm?, bindingResult: BindingResult): String {
        val errors = personForm?.validate() ?: emptyList()

        for (error in errors) {
            bindingResult.rejectValue(error.first, "error.personForm", error.second)
        }

        return if (bindingResult.hasErrors()) {
            "form"
        } else "redirect:/results"
    }

    @GetMapping("/enter-number/{number}")
    fun enterNumber(@PathVariable number: Int,
                    redirectAttributes: RedirectAttributes): String {
        // Add the number as a flash attribute
        redirectAttributes.addFlashAttribute("enteredNumber", number)

        // Redirect to the /get-number endpoint
        return "redirect:/get-number"
    }

    @GetMapping("/get-number")
    fun getNumber(model: Model): String {
        val number = model.asMap()["enteredNumber"] as Int?
        if (number != null) {
            log.info("Received number: $number")
        } else {
            log.warn("No number received.")
        }
        return "number-page"
    }
}
