package io.spring.shoestore.core.variants

enum class VariantColor(val code: String) {
    WHITE("White"),
    GREEN("Green"),
    BLACK("Black"),
    BLUE("Blue"),
    RED("Red"),
    NA("N/A");
    companion object {
        fun lookup(code: String): VariantColor? {
            return values().find { it.code == code }
        }
    }

}