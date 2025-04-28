package ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import domain.usecase.FormatResponseUseCase
import domain.usecase.GenerateRequestUseCase
import domain.usecase.LoadPortsUseCase
import domain.usecase.SendRequestUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import state.RequestState

class RequestViewModel(
    private val sendRequestUseCase: SendRequestUseCase,
    private val generateRequestUseCase: GenerateRequestUseCase,
    private val formatResponseUseCase: FormatResponseUseCase,
    private val loadPortsUseCase: LoadPortsUseCase
) {
    var state by mutableStateOf(RequestState())
        private set
        
    private val scope = CoroutineScope(Dispatchers.IO)
    
    init {
        loadPorts()
    }
    
    fun loadPorts() {
        val ports = loadPortsUseCase.execute(state.showAllPorts)
        state = state.copy(
            ports = ports.associateBy { it.systemName },
            selectedPort = ports.firstOrNull()?.systemName ?: ""
        )
    }
    
    fun toggleShowAllPorts() {
        state = state.copy(showAllPorts = !state.showAllPorts)
        loadPorts()
    }
    
    fun updateState(newState: RequestState) {
        state = newState
    }
    
    fun sendRawRequest() {
        scope.launch {
            try {
                val bytes = state.rawRequest.split(" ")
                    .filter { it.isNotBlank() }
                    .map { it.toInt(16).toByte() }
                    .toByteArray()

                val response = sendRequestUseCase.sendRequest(
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
            }
        }
    }

    fun sendGeneratedRequest() {
        scope.launch {
            try {
                val functionCode = when (state.requestType) {
                    "Read Holding Registers" -> 0x03
                    "Write Single Register" -> 0x06
                    else -> 0x03
                }

                val slave = state.slaveAddress.toIntOrNull()?.takeIf { it in 1..247 } ?: 1
                val address = state.address.toIntOrNull() ?: 0
                val quantity = state.quantity.toIntOrNull() ?: 1

                val request = generateRequestUseCase.execute(slave, functionCode, address, quantity)

                val response = sendRequestUseCase.sendRequest(
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
            }
        }
    }

    fun getFormattedResponse(): String {
        return formatResponseUseCase.execute(
            response = state.response,
            displayMode = state.displayMode,
            byteOrder = state.byteOrder
        )
    }
} 