    package io.spring.shoestore.app.http

    import io.spring.shoestore.app.exception.AppException
    import io.spring.shoestore.core.exceptions.QuantityExceedsAvailabilityException
    import io.spring.shoestore.core.orders.StockInsufficientException
    import jakarta.servlet.RequestDispatcher
    import jakarta.servlet.http.HttpServletRequest
    import org.springframework.boot.web.servlet.error.ErrorController
    import org.springframework.http.HttpStatus
    import org.springframework.stereotype.Controller
    import org.springframework.ui.Model
    import org.springframework.web.bind.annotation.ControllerAdvice
    import org.springframework.web.bind.annotation.ExceptionHandler
    import org.springframework.web.bind.annotation.RequestMapping

    @Controller
    @ControllerAdvice
    class CustomErrorController : ErrorController {

        @ExceptionHandler(AppException::class)
        fun handleAppError(ex: AppException, model: Model): String {
            model.addAttribute("errorMessage", ex.message)
            return "error"
        }

        @ExceptionHandler(StockInsufficientException::class)
        fun handleStockError(ex: StockInsufficientException, model: Model): String {
            model.addAttribute("errorMessage", ex.message)
            return "error"
        }

        @ExceptionHandler(QuantityExceedsAvailabilityException::class)
        fun handleQuantityExceedsAvailabilityError(ex: QuantityExceedsAvailabilityException, model: Model): String {
            model.addAttribute("errorMessage", ex.message)
            return "error" // or redirect to a specific page
        }

        @RequestMapping("/error")
        fun handleError(request: HttpServletRequest, model: Model): String {
            val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
            if (status != null) {
                val statusCode = Integer.valueOf(status.toString())
                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    model.addAttribute("errorMessage", "Page not found")
                } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    model.addAttribute("errorMessage", "Internal server error")
                }
            }
            return "error"
        }

        fun getErrorPath(): String {
            return "/error"
        }

    }
