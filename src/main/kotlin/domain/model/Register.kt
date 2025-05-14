package domain.model

data class Register(
    val address: Int,
    val name: String,
    val description: String,
    val type: RegisterType,
    val readOnly: Boolean = true
)

enum class RegisterType {
    ANALOG, CONFIG, CRM, DISCRETE
}