package viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fazecast.jSerialComm.SerialPort
import model.Modbus
import state.ByteOrder
import state.ModbusUiState
import state.DisplayMode
import ui.components.NotificationManager
import java.nio.ByteBuffer


class MainViewModel {

    var state by mutableStateOf(ModbusUiState())
        private set

    private val modbus = Modbus()

    fun loadPorts() {
        val portsMap = SerialPort.getCommPorts()
            .filter {
                state.showAllPorts || it.descriptivePortName.contains("CP21", ignoreCase = true)
            }
            .associate { port ->
                port.systemPortName to "${port.systemPortName} (${port.descriptivePortName})"
            }
        state = state.copy(
            ports = portsMap,
            selectedPort = portsMap.keys.firstOrNull() ?: ""
        )
    }

    fun update(modifier: ModbusUiState.() -> ModbusUiState) {
        state = state.modifier()
    }


    fun saveConnectionSettings() {
        val address = state.slaveAddress.toIntOrNull()
        if (address in 1..247) {
            NotificationManager.show("Настройки сохранены")
        } else {
            NotificationManager.show("Ошибка: Неверный адрес")
        }
    }


    fun sendRawRequest() {
        try {
            val bytes = state.rawRequest.split(" ")
                .filter { it.isNotBlank() }
                .map { it.toInt(16).toByte() }
                .toByteArray()

            val response = modbus.sendRequest(
                portName = state.selectedPort,
                baudRate = state.baudRate.toInt(),
                dataBits = state.dataBits.toInt(),
                stopBits = state.stopBits.toInt(),
                request = bytes
            )

            val hexRequest = bytes.joinToString(" ") { "%02X".format(it) }
            val hex = response.joinToString(" ") { "%02X".format(it) }

            state = state.copy(response = hex, error = null, lastRequestHex = hexRequest)

        } catch (e: Exception) {
            state = state.copy(error = "Ошибка: ${e.message}")
            println("Ошибка: ${e.message}")
        }
    }

    fun sendGeneratedRequest() {
        try {
            val functionCode = when (state.requestType) {
                "Read Holding Registers" -> 0x03
                "Write Single Register" -> 0x06
                else -> 0x03
            }

            val slave = state.slaveAddress.toIntOrNull()?.takeIf { it in 1..247 } ?: 1
            val address = state.address.toIntOrNull() ?: 0
            val quantity = state.quantity.toIntOrNull() ?: 1

            val request = modbus.generateRequest(slave, functionCode, address, quantity)

            val response = modbus.sendRequest(
                portName = state.selectedPort,
                baudRate = state.baudRate.toInt(),
                dataBits = state.dataBits.toInt(),
                stopBits = state.stopBits.toInt(),
                request = request
            )

            val hex = response.joinToString(" ") { "%02X".format(it) }
            val hexRequest = request.joinToString(" ") { "%02X".format(it) }

            state = state.copy(response = hex, error = null, lastRequestHex = hexRequest)

        } catch (e: Exception) {
            state = state.copy(error = "Ошибка: ${e.message}")
            println("Ошибка: ${e.message}")
        }
    }

    fun getFormattedResponse() : String {
        val bytes = state.response
            .split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull(16)?.toByte() }

        return when (state.displayMode) {
            DisplayMode.HEX -> state.response

            DisplayMode.DEC -> {
                val data = bytes.drop(3).dropLast(2)
                data.chunked(2).mapIndexed { index, pair ->
                    val value = ((pair.getOrNull(0)?.toInt() ?: 0) shl 8) +
                            (pair.getOrNull(1)?.toInt() ?: 0)
                    "Регистр $index = $value"
                }.joinToString("\n")
            }

            DisplayMode.FLOAT -> {
                val data = bytes.drop(3).dropLast(2)

                data.chunked(4).mapIndexed { index, group ->
                    if (group.size == 4) {

                        val byteArray = when (state.byteOrder) {
                            ByteOrder.ABCD -> byteArrayOf(group[0], group[1], group[2], group[3])
                            ByteOrder.CDAB -> byteArrayOf(group[2], group[3], group[0], group[1])
                            ByteOrder.BADC -> byteArrayOf(group[1], group[0], group[3], group[2])
                            ByteOrder.DCBA -> byteArrayOf(group[3], group[2], group[1], group[0])
                        }

                        try {
                            val float = java.nio.ByteBuffer.wrap(byteArray).float
                            "Float $index = %.5f".format(float)
                        } catch (e: Exception) {
                            "Float $index = Ошибка декодирования"
                        }
                    } else {
                        "Float $index = ???"
                    }
                }.joinToString("\n")
            }
        }
    }

    fun sendRequest(request: ByteArray) {
        try {
            val port = SerialPort.getCommPort(state.selectedPort)
            port.setComPortParameters(
                state.baudRate.toInt(),
                state.dataBits.toInt(),
                state.stopBits.toInt(),
                0 // паритет: None
            )
            port.openPort()
            port.writeBytes(request, request.size)

            Thread.sleep(20)
            val buffer = ByteArray(256)
            val read = port.readBytes(buffer, buffer.size)
            port.closePort()

            val responseHex = buffer.take(read).joinToString(" ") { "%02X".format(it) }

            state = state.copy(response = responseHex, error = null)

        } catch (e: Exception) {
            state = state.copy(error = "Ошибка: ${e.message}")
            println("Ошибка: ${e.message}")
        }
    }
}
