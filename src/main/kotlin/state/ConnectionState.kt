package state

import domain.model.PortConfig
import domain.model.PortInfo

data class ConnectionState(
    val ports: Map<String, PortInfo> = emptyMap(),
    val selectedPort: String = "",
    val slaveAddress: String = "1",
    val baudRate: String = "38400",
    val dataBits: String = "8",
    val stopBits: String = "1",
    val showAllPorts: Boolean = false,
    val testResponse: String? = null,
    val error: String? = null
) {
    fun toPortConfig(): PortConfig {
        return PortConfig(
            portName = selectedPort,
            baudRate = baudRate.toInt(),
            dataBits = dataBits.toInt(),
            stopBits = stopBits.toInt(),
        )
    }
} 