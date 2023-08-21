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
//        val partnerCode = "MOMOMWNB20210129"
//        val accessKey = "nkDyGIefYvOL9Nyg"
//        val secretKey = "YaCm3DJAJuAV9jGmwauQ0mwT6FpqYiOI"
//        val endPoint = "https://test-payment.momo.vn/v2/gateway/api"
//
//        val partnerInfo = PartnerInfo(partnerCode, accessKey, secretKey)
//        log.info("partnerInfo $partnerInfo")
//        val test = 123();
//        log.info("partnerInfo $partnerInfo")

//        val test = Test789()
//        val hello = test.main()
//
//        log.info("hello $hello")

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

//    @GetMapping("/momo")
//    fun testMomo(model: Model): String {
//        val username: String = "hukhho"
//
//        val requestId = System.currentTimeMillis().toString()
//
//        val orderId = System.currentTimeMillis().toString() + "_InvoiceID"
//
//
//        val returnURL = "http://localhost:3000/payment/queryPayment"
//        val notifyURL = "http://localhost:3000/payment/queryPayment"
//
//
//        val partnerCode = "MOMOMWNB20210129"
//        val accessKey = "nkDyGIefYvOL9Nyg"
//        val secretKey = "YaCm3DJAJuAV9jGmwauQ0mwT6FpqYiOI"
//        val endPoint = "https://test-payment.momo.vn/v2/gateway/api"
//
//        val partnerInfo = PartnerInfo(partnerCode, accessKey, secretKey)
//
//        val environment: Environment = Environment.selectEnv("dev")
//
//        environment.partnerInfo = partnerInfo
//
//        val captureWalletMoMoResponse: PaymentResponse = CreateOrderMoMo.process(
//            environment,
//            orderId,
//            requestId,
//            java.lang.Long.toString(50000L),
//            username,
//            returnURL,
//            notifyURL,
//            "",
//            RequestType.CAPTURE_WALLET
//        )
//        //PaymentResponse captureATMMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(amount), orderInfo, returnURL, notifyURL, "", RequestType.PAY_WITH_ATM);
//        //PaymentResponse captureCreditMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(amount), orderInfo, returnURL, notifyURL, "", RequestType.PAY_WITH_CREDIT);
//        //PaymentResponse captureATMMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(amount), orderInfo, returnURL, notifyURL, "", RequestType.PAY_WITH_ATM);
//        //PaymentResponse captureCreditMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(amount), orderInfo, returnURL, notifyURL, "", RequestType.PAY_WITH_CREDIT);
//
//        val redirectLink: String = captureWalletMoMoResponse.getPayUrl().toString()
//
//        return "redirect:$redirectLink"
//    }
//    @GetMapping("/profile")
//    fun profile(
//        model: Model,
//        @AuthenticationPrincipal oidcUser: OidcUser
//    ): String? {
//        model.addAttribute("profile", oidcUser.claims)
//        model.addAttribute("profileJson", claimsToJson(oidcUser.claims))
//        return "profile"
//    }

//    private fun claimsToJson(claims: Map<String, Any>): String? {
//        try {
//            return objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(claims)
//        } catch (jpe: JsonProcessingException) {
//            log.error("Error parsing claims to JSON", jpe)
//        }
//        return "Error parsing claims to JSON."
//    }
//
//    @Bean
//    fun objectMapper(): ObjectMapper {
//        val module = JavaTimeModule()
//        return ObjectMapper()
//            .registerModule(module)
//    }

}
