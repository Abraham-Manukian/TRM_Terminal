package state

data class ModbusUiState(
    val ports: List<String> = emptyList(),
    val selectedPort: String = "",
    val baudRate: String = "38400",
    val dataBits: String = "8",
    val stopBits: String = "1",
    val requestType: String = "Read Holding Registers",
    val address: String = "4102",
    val quantity: String = "2",
    val rawRequest: String = "",
    val response: String = "",
    val lastRequestHex: String = "",
    val error: String? = null,
    val displayMode: DisplayMode = DisplayMode.HEX
)