package domain.model

data class PortConfig(
    val portName: String,
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: Int,
    val parity: Int = 0,
    val attemptCount: Int = 3,
    val timeoutRead: Int = 200,
    val timeoutWrite: Int = 200,
    val slaveId: Byte = 1,
    val pollIntervalMillis: Long = 1000L
)