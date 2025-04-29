package domain.model

data class Register(
    val address: Int,
    val name: String,
    val description: String = "",
    var value: String = "",
    var isSelected: Boolean = false
)
