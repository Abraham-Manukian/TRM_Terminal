package ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import domain.model.PortConfig
import domain.model.Register
import domain.usecase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import state.ByteOrder
import state.DisplayMode
import state.RequestState
import state.toPortConfigOrNull
import ui.components.NotificationManager

class RequestViewModel(
    private val updatePortConfig: UpdatePortConfigUseCase,
    private val startPolling: StartSingleRegisterPollingUseCase,
    private val stopPolling: StopSingleRegisterPollingUseCase,
    private val readOnce: ReadRegisterOnceUseCase,
    private val writeRegister: WriteRegisterUseCase,
    private val saveConnectionSettings: SaveConnectionSettingsUseCase,
    private val frequencyRegister: Register,
    private val connectionViewModel: ConnectionViewModel
) : ScreenModel {

    private val _state = MutableStateFlow(RequestState())
    val state: StateFlow<RequestState> = _state

    private val current get() = _state.value

    private fun updateState(update: (RequestState) -> RequestState) {
        _state.update(update)
    }

    init {
        connectionViewModel.state
            .map { it.toPortConfig() }
            .onEach { cfg -> saveConnectionSettings(cfg) }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun readOnce() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readOnce(frequencyRegister)
            result
                .onSuccess { value ->
                    updateState { it.copy(response = value.toString(), error = null) }
                }
                .onFailure { error ->
                    updateState { it.copy(error = error.message ?: "Ошибка чтения") }
                }
        }
    }

    fun toggleAutoRequest() {
        if (state.value.isAutoRequestRunning) {
            stopAutoRequest()
        } else {
            startAutoRequest()
        }
    }

    fun startAutoRequest() {
        println("State at submit: ${connectionViewModel.state.value}")
        val config = connectionViewModel.state.value.toPortConfigOrNull()
        if (config == null) {
            NotificationManager.show("Сначала настройте подключение")
            return
        }
        updatePortConfig(config)

        updateState { it.copy(isAutoRequestRunning = true) }
        startPolling(frequencyRegister) { value ->
            updateState { it.copy(response = value.toString(), error = null) }
        }
    }

    fun stopAutoRequest() {
        updateState { it.copy(isAutoRequestRunning = false) }
        stopPolling(frequencyRegister)
        NotificationManager.show("Автоопрос остановлен")
    }

    fun setByteOrder(order: ByteOrder) {
        updateState { it.copy(byteOrder = order) }
    }

    fun setByteOrder(label: String) {
        val order = ByteOrder.values().find { it.label == label } ?: ByteOrder.ABCD
        updateState { it.copy(byteOrder = order) }
    }

    fun setDisplayMode(mode: DisplayMode) {
        updateState { it.copy(displayMode = mode) }
    }

    fun getModeDescription(mode: DisplayMode): String {
        return when (mode) {
            DisplayMode.DEC -> "Десятичный"
            DisplayMode.HEX -> "Шестнадцатеричный"
            DisplayMode.FLOAT -> "Плавающая точка"
        }
    }

    fun setRawRequest(value: String) {
        updateState { it.copy(rawRequest = value) }
    }

    fun getFormattedResponse(): String {
        val float = state.value.response.toFloatOrNull() ?: return "Ошибка"
        return when (state.value.displayMode) {
            DisplayMode.DEC -> "Значение: ${float}"
            DisplayMode.HEX -> "0x%08X".format(java.lang.Float.floatToIntBits(float))
            DisplayMode.FLOAT -> "Плавающая: $float"
        }
    }

}
