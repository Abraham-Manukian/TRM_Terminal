package ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import domain.usecase.FormatResponseUseCase
import domain.usecase.GenerateRequestUseCase
import domain.usecase.LoadPortsUseCase
import domain.usecase.SendRawRequestUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import state.ByteOrder
import state.DisplayMode
import state.RequestState
import ui.components.NotificationManager
import kotlin.onFailure


class RequestViewModel(
    private val sendRawRequestUseCase: SendRawRequestUseCase,
    private val generateRequestUseCase: GenerateRequestUseCase,
    private val formatResponseUseCase: FormatResponseUseCase,
    private val loadPortsUseCase: LoadPortsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(RequestState())
    val state: StateFlow<RequestState> = _state

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isRequestInProgress = false

    init {
        loadPorts()
    }

    private val current get() = _state.value

    private fun updateState(update: (RequestState) -> RequestState) {
        _state.update(update)
    }

    fun loadPorts() {
        val ports = loadPortsUseCase.execute(current.connectionState.showAllPorts)
        val firstPort = ports.firstOrNull()?.systemName.orEmpty()

        updateState { state ->
            state.copy(
                connectionState = state.connectionState.copy(
                    ports = ports.associateBy { it.systemName },
                    selectedPort = firstPort
                )
            )
        }
    }

    fun setByteOrder(order: ByteOrder) {
        _state.update { it.copy(byteOrder = order) }
    }

    fun setDisplayMode(mode: DisplayMode) {
        _state.update { it.copy(displayMode = mode) }
    }

    fun getModeDescription(mode: DisplayMode): String {
        return when (mode) {
            DisplayMode.DEC -> "Десятичный"
            DisplayMode.HEX -> "Шестнадцатеричный"
            DisplayMode.FLOAT -> "Плавающая точка"
        }
    }

    fun setByteOrder(label: String) {
        val order = ByteOrder.values().find { it.label == label } ?: ByteOrder.ABCD
        updateState { it.copy(byteOrder = order) }
    }

    fun setRawRequest(value: String) {
        updateState { it.copy(rawRequest = value) }
    }

    fun toggleShowAllPorts() {
        updateState { state ->
            val toggled = state.connectionState.copy(
                showAllPorts = !state.connectionState.showAllPorts
            )
            state.copy(connectionState = toggled)
        }
        loadPorts()
    }

    fun sendGeneratedRequest() {
        if (isRequestInProgress) {
            NotificationManager.show("Дождитесь завершения предыдущего запроса")
            return
        }

        isRequestInProgress = true
        updateState { it.copy(error = null) }

        scope.launch {
            try {
                val type = current.requestType
                val functionCode = when (type) {
                    "Read Holding Registers" -> 0x03
                    "Write Single Register" -> 0x06
                    else -> 0x03
                }

                val slave = current.connectionState.slaveAddress.toIntOrNull()?.coerceIn(1..247) ?: 1
                val address = current.address.toIntOrNull() ?: 0
                val quantity = current.quantity.toIntOrNull() ?: 1

                val request = generateRequestUseCase.execute(slave, functionCode, address, quantity)
                val config = current.connectionState.toPortConfig()

                val result = sendRawRequestUseCase(request.joinToString(" ") { "%02X".format(it) }, config)

                result.onSuccess { (req, resp) ->
                    updateState {
                        it.copy(
                            lastRequestHex = req.joinToString(" ") { b -> "%02X".format(b) },
                            response = resp.joinToString(" ") { b -> "%02X".format(b) },
                            error = null
                        )
                    }
                    NotificationManager.show("Запрос успешно отправлен")
                }.onFailure { throwable ->
                    updateState { it.copy(error = "Ошибка: ${throwable.message}") }
                    NotificationManager.show("Ошибка: ${throwable.message}")
                }
            } catch (e: Exception) {
                updateState { it.copy(error = "Ошибка: ${e.message}") }
                NotificationManager.show("Ошибка: ${e.message}")
            } finally {
                isRequestInProgress = false
            }
        }
    }

    fun sendRawRequest() {
        scope.launch {
            try {
                val config = current.connectionState.toPortConfig()
                val raw = current.rawRequest

                val result = sendRawRequestUseCase(raw, config)

                result.onSuccess { (req, resp) ->
                    updateState {
                        it.copy(
                            lastRequestHex = req.joinToString(" ") { b -> "%02X".format(b) },
                            response = resp.joinToString(" ") { b -> "%02X".format(b) },
                            error = null
                        )
                    }
                    NotificationManager.show("Запрос успешно отправлен")
                }.onFailure { throwable ->
                    updateState { it.copy(error = "Ошибка: ${throwable.message}") }
                    NotificationManager.show("Ошибка: ${throwable.message}")
                }
            } catch (e: Exception) {
                updateState { it.copy(error = "Ошибка: ${e.message}") }
                NotificationManager.show("Ошибка: ${e.message}")
            } finally {
                isRequestInProgress = false
            }
        }
    }

    fun getFormattedResponse(): String {
        return formatResponseUseCase.execute(
            response = current.response,
            displayMode = current.displayMode,
            byteOrder = current.byteOrder
        )
    }
} 