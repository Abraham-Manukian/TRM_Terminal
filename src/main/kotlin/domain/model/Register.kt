package model

data class Register(
    val code: String,
    val description: String,
    var isSelected: Boolean = false
)
