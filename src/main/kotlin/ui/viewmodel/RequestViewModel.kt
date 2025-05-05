package ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import domain.usecase.FormatResponseUseCase
import domain.usecase.GenerateRequestUseCase
import domain.usecase.LoadPortsUseCase
import domain.usecase.SendRawRequestUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import state.RequestState
import ui.components.NotificationManager
import kotlin.onFailure

class RequestViewModel(
    private val sendRawRequestUseCase: SendRawRequestUseCase,
    private val generateRequestUseCase: GenerateRequestUseCase,
    private val formatResponseUseCase: FormatResponseUseCase,
    private val loadPortsUseCase: LoadPortsUseCase
) : KoinComponent {

    val state = mutableStateOf(RequestState())

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isRequestInProgress = false

    init {
        loadPorts()
    }

    fun loadPorts() {
        val showAll = state.value.connectionState.showAllPorts
        val ports = loadPortsUseCase.execute(showAll)
        val firstPort = ports.firstOrNull()?.systemName.orEmpty()

        val newConnectionState = state.value.connectionState.copy(
            ports = ports.associateBy { it.systemName },
            selectedPort = firstPort
        )

        state.value = state.value.copy(connectionState = newConnectionState)
    }

    fun toggleShowAllPorts() {
        val toggled = state.value.connectionState.copy(
            showAllPorts = !state.value.connectionState.showAllPorts
        )
        state.value = state.value.copy(connectionState = toggled)
        loadPorts()
    }

    fun sendGeneratedRequest() {
        if (isRequestInProgress) {
            NotificationManager.show("Дождитесь завершения предыдущего запроса")
            return
        }

        isRequestInProgress = true
        state.value = state.value.copy(error = null)

        scope.launch {
            try {
                val type = state.value.requestType
                val functionCode = when (type) {
                    "Read Holding Registers" -> 0x03
                    "Write Single Register" -> 0x06
                    else -> 0x03
                }

                val slave = state.value.connectionState.slaveAddress.toIntOrNull()?.coerceIn(1..247) ?: 1
                val address = state.value.address.toIntOrNull() ?: 0
                val quantity = state.value.quantity.toIntOrNull() ?: 1

                val request = generateRequestUseCase.execute(slave, functionCode, address, quantity)
                val config = state.value.connectionState.toPortConfig()

                val result = sendRawRequestUseCase(request.joinToString(" ") { "%02X".format(it) }, config)

                result.onSuccess { pair ->
                    val (req, resp) = pair
                    state.value = state.value.copy(
                        lastRequestHex = req.joinToString(" ") { "%02X".format(it) },
                        response = resp.joinToString(" ") { "%02X".format(it) },
                        error = null
                    )
                    NotificationManager.show("Запрос успешно отправлен")
                }.onFailure {
                    state.value = state.value.copy(error = "Ошибка: ${it.message}")
                    NotificationManager.show("Ошибка: ${it.message}")
                }
            } catch (e: Exception) {
                state.value = state.value.copy(error = "Ошибка: ${e.message}")
                NotificationManager.show("Ошибка: ${e.message}")
            } finally {
                isRequestInProgress = false
            }
        }
    }

    fun sendRawRequest() {
        scope.launch {
            try {
                val config = state.value.connectionState.toPortConfig()
                val raw = state.value.rawRequest

                val result = sendRawRequestUseCase(raw, config)

                result.onSuccess { pair ->
                    val (req, resp) = pair
                    state.value = state.value.copy(
                        lastRequestHex = req.joinToString(" ") { "%02X".format(it) },
                        response = resp.joinToString(" ") { "%02X".format(it) },
                        error = null
                    )
                }.onFailure {
                    state.value = state.value.copy(error = "Ошибка: ${it.message}")
                }
            } catch (e: Exception) {
                state.value = state.value.copy(error = "Ошибка: ${e.message}")
            }
        }
    }

    fun getFormattedResponse(): String {
        return formatResponseUseCase.execute(
            response = state.value.response,
            displayMode = state.value.displayMode,
            byteOrder = state.value.byteOrder
        )
    }
} 