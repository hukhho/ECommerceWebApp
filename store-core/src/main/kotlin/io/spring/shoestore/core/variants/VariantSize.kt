package io.spring.shoestore.core.variants

enum class VariantSize(val code: String) {
    VN32("VN 32"),
    VN33("VN 33"),
    VN34("VN 34"),
    VN35("VN 35"),
    VN36("VN 36"),
    VN37("VN 37"),
    VN38("VN 38"),
    VN39("VN 39"),
    VN40("VN 40"),
    VN41("VN 41"),
    VN42("VN 42"),
    VN43("VN 43"),
    VN44("VN 44"),
    VN45("VN 45"),
    VN46("VN 46"),

    XS("XS"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL"),

    NA("N/A");
    companion object {
        fun lookup(code: String): VariantSize? {
            return values().find { it.code == code }
        }
    }
}