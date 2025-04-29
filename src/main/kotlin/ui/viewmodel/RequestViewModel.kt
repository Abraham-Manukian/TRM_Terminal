package ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import domain.usecase.FormatResponseUseCase
import domain.usecase.GenerateRequestUseCase
import domain.usecase.LoadPortsUseCase
import domain.usecase.SendRequestUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import state.RequestState
import ui.components.NotificationManager

/**
 * ViewModel для экрана отправки запросов
 */
class RequestViewModel(
    private val sendRequestUseCase: SendRequestUseCase,
    private val generateRequestUseCase: GenerateRequestUseCase,
    private val formatResponseUseCase: FormatResponseUseCase,
    private val loadPortsUseCase: LoadPortsUseCase
) : KoinComponent {
    // Состояние представления с использованием mutableStateOf для реактивности
    private val _state = mutableStateOf(RequestState())
    val state: RequestState by _state
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    init {
        loadPorts()
    }
    
    fun loadPorts() {
        val ports = loadPortsUseCase.execute(state.showAllPorts)
        updateState(state.copy(
            ports = ports.associateBy { it.systemName },
            selectedPort = ports.firstOrNull()?.systemName ?: ""
        ))
    }
    
    fun toggleShowAllPorts() {
        updateState(state.copy(showAllPorts = !state.showAllPorts))
        loadPorts()
    }
    
    /**
     * Обновление состояния
     */
    fun updateState(newState: RequestState) {
        _state.value = newState
    }
    
    /**
     * Отправка сгенерированного запроса
     */
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

                updateState(state.copy(response = hex, error = null, lastRequestHex = hexRequest))
                NotificationManager.show("Запрос успешно отправлен")
            } catch (e: Exception) {
                updateState(state.copy(error = "Ошибка: ${e.message}"))
                NotificationManager.show("Ошибка: ${e.message}")
            }
        }
    }
    
    /**
     * Отправка произвольного запроса, введенного пользователем
     */
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

                updateState(state.copy(response = hex, error = null, lastRequestHex = hexRequest))
                NotificationManager.show("Ручной запрос успешно отправлен")
            } catch (e: Exception) {
                updateState(state.copy(error = "Ошибка: ${e.message}"))
                NotificationManager.show("Ошибка: ${e.message}")
            }
        }
    }
    
    /**
     * Получение форматированного ответа согласно выбранному режиму отображения
     */
    fun getFormattedResponse(): String {
        val response = state.response
        val displayMode = state.displayMode
        val byteOrder = state.byteOrder
        
        return formatResponseUseCase.execute(
            response = response,
            displayMode = displayMode,
            byteOrder = byteOrder
        )
    }
} 