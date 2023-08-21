import io.spring.shoestore.core.users.UserId
import java.util.*

data class PlaceOrderCommand(
    val orderId: UUID,
    val userId: UserId,
    val items: List<Pair<String, Int>>?,
    val shippingDetails: ShippingDetails,
    val paymentMethod: String
)
data class ShippingDetails(
    val receiverName: String,

    val receiverPhone: String,

    val note: String?,

    val shippingStreet: String,

    val shippingWard: String,

    val shippingDistrict: String,

    val shippingProvince: String
) {
    fun validate(): List<Pair<String, String>> {
        val errors = mutableListOf<Pair<String, String>>()

        // Ensure receiverName is not null and has more than 3 letters
        if (receiverName == null || receiverName.length <= 3) {
            errors.add(Pair("receiverName", "Name must be more than 3 letters."))
        } else if (receiverName.matches(Regex("^\\d+$"))) {
            errors.add(Pair("receiverName", "Name should not be just numbers."))
        }

        // Validate phone number format
        if (receiverPhone == null || !receiverPhone.matches(Regex("^\\+?[0-9]{10,15}$"))) {
            errors.add(Pair("receiverPhone", "Phone number format is invalid."))
        }

        // Ensure note is not excessively long
        note?.let {
            if (it.length > 200) {
                errors.add(Pair("note", "Note is too long. Maximum 200 characters allowed."))
            }
        }

        // Validate address components for special characters and null values
        val addressRegex = Regex("^[a-zA-Z0-9 ]+$")
        if (shippingStreet == null || !shippingStreet.matches(addressRegex)) {
            errors.add(Pair("shippingStreet", "Street should only contain letters, numbers, and spaces."))
        }
        if (shippingWard == null || !shippingWard.matches(addressRegex)) {
            errors.add(Pair("shippingWard", "Ward should only contain letters, numbers, and spaces."))
        }
        if (shippingDistrict == null || !shippingDistrict.matches(addressRegex)) {
            errors.add(Pair("shippingDistrict", "District should only contain letters, numbers, and spaces."))
        }
        if (shippingProvince == null || !shippingProvince.matches(addressRegex)) {
            errors.add(Pair("shippingProvince", "Province should only contain letters, numbers, and spaces."))
        }

        return errors
    }
}