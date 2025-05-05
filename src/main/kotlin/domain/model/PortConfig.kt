package domain.model

data class PortConfig(
    val portName: String,
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: Int
)
