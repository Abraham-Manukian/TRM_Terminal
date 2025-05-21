package state

import domain.model.PortConfig
import domain.model.PortInfo

data class ConnectionState(
    val ports: Map<String, PortInfo> = emptyMap(),
    val selectedPort: String          = "",
    val slaveAddress: String          = "1",      // строковый ввод slave ID
    val baudRate: String              = "38400",
    val dataBits: String              = "8",
    val stopBits: String              = "1",
    val parity: String                = "NONE",
    val attemptCount: String          = "3",
    val timeoutRead: String           = "200",
    val timeoutWrite: String          = "200",
    val pollIntervalMillis: Long      = 1000L,
    val isConnected: Boolean = false,
    val showAllPorts: Boolean = false,
    val testResponse: String? = null,
    val error: String?        = null
) {
    fun toPortConfig(): PortConfig = PortConfig(
        portName           = selectedPort,
        baudRate           = baudRate.toInt(),
        dataBits           = dataBits.toInt(),
        stopBits           = stopBits.toInt(),
        parity             = when (parity) {
            "EVEN" -> 1
            "ODD"  -> 2
            else   -> 0
        },
        attemptCount       = attemptCount.toInt(),
        timeoutRead        = timeoutRead.toInt(),
        timeoutWrite       = timeoutWrite.toInt(),
        slaveId            = slaveAddress.toByte(),
        pollIntervalMillis = pollIntervalMillis
    )
}
