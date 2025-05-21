package state

import domain.model.PortConfig

fun ConnectionState.toPortConfigOrNull(): PortConfig? {
    val portName = selectedPort.ifBlank { return null }
    val baudRate = baudRate.toIntOrNull() ?: return null
    val dataBits = dataBits.toIntOrNull() ?: return null
    val stopBits = stopBits.toIntOrNull() ?: return null
    val slave = slaveAddress.toIntOrNull() ?: return null
    val parityValue = when (parity.uppercase()) {
        "EVEN" -> 1
        "ODD"  -> 2
        else   -> 0
    }

    val attempt = attemptCount.toIntOrNull() ?: return null
    val timeoutR = timeoutRead.toIntOrNull() ?: return null
    val timeoutW = timeoutWrite.toIntOrNull() ?: return null

    return PortConfig(
        portName = portName,
        baudRate = baudRate,
        dataBits = dataBits,
        stopBits = stopBits,
        parity = parityValue,
        slaveId = slave.toByte(),
        attemptCount = attempt,
        timeoutRead = timeoutR,
        timeoutWrite = timeoutW,
        pollIntervalMillis = pollIntervalMillis
    )
}
