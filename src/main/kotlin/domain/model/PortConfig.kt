package domain.model

data class PortConfig(
    val portName: String,
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: Int,
    val parity: Int,
    val attemptCount: Int,
    val timeoutRead: Int,
    val timeoutWrite: Int,
    val slaveId: Byte,
    val pollIntervalMillis: Long
)