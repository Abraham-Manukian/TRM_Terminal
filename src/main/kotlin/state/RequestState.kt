package state

import domain.model.PortInfo

data class RequestState(
    val ports: Map<String, PortInfo> = emptyMap(),
    val selectedPort: String = "",
    val baudRate: String = "38400",
    val dataBits: String = "8", 
    val stopBits: String = "1",
    val showAllPorts: Boolean = false,
    val requestType: String = "Read Holding Registers",
    val address: String = "4102",
    val slaveAddress: String = "1",
    val quantity: String = "2",
    val rawRequest: String = "01 03 10 06 00 02 65 60",
    val response: String = "",
    val lastRequestHex: String = "",
    val error: String? = null,
    val displayMode: DisplayMode = DisplayMode.HEX,
    val byteOrder: ByteOrder = ByteOrder.ABCD
) 