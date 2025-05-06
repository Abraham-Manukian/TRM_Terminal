package state

data class RequestState(
    val connectionState: ConnectionState = ConnectionState(),
    val requestType: String = "Read Holding Registers",
    val address: String = "4102",
    val quantity: String = "2",
    val rawRequest: String = "01 03 10 06 00 02 65 60",
    val response: String = "",
    val lastRequestHex: String = "",
    val error: String? = null,
    var displayMode: DisplayMode = DisplayMode.HEX,
    val isRequestInProgress: Boolean = false,
    val byteOrder: ByteOrder = ByteOrder.ABCD,
    val isAutoRequestRunning: Boolean = false
)