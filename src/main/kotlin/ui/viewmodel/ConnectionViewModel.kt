package ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import domain.usecase.LoadPortsUseCase
import domain.usecase.SaveConnectionSettingsUseCase
import domain.usecase.StartPollingUseCase
import domain.usecase.StopPollingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import state.ConnectionState

class ConnectionViewModel(
    private val loadPortsUseCase: LoadPortsUseCase,
    private val saveConnectionSettingsUseCase: SaveConnectionSettingsUseCase,
    private val startPollingUseCase: StartPollingUseCase,
    private val stopPollingUseCase: StopPollingUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(ConnectionState())
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        loadPorts()
    }

    private fun update(f: ConnectionState.() -> ConnectionState) {
        _state.update { it.f() }
    }

    /** Загружает список COM-портов заново */
    fun loadPorts() {
        scope.launch {
            runCatching {
                loadPortsUseCase.execute(state.value.showAllPorts)
            }.onSuccess { list ->
                update { copy(ports = list.associateBy { it.systemName }) }
            }.onFailure {
                update { copy(error = "Не удалось загрузить порты: ${it.message}") }
            }
        }
    }

    /** Переключает флаг, показывать ли все порты, и перезагружает их */
    fun toggleShowAllPorts() {
        update { copy(showAllPorts = !showAllPorts) }
        loadPorts()
    }

    /** Сохраняет текущие настройки (но не подключается) */
    fun saveConnectionSettings() {
        val cfg = state.value.toPortConfig()
        scope.launch {
            runCatching {
                saveConnectionSettingsUseCase.invoke(cfg)
            }.onFailure {
                update { copy(error = "Ошибка сохранения: ${it.message}") }
            }
        }
    }

    fun sendTestRequest() {
        val cfg = _state.value.toPortConfig()
        scope.launch {
            runCatching {
                // Здесь можно вызвать реальный use-case для теста,
                // пока просто возвращаем строку-симуляцию:
                "Соединение проверено: ${cfg.portName}"
            }.onSuccess { result ->
                update { copy(testResponse = result, error = null) }
            }.onFailure {
                update { copy(error = "Ошибка теста: ${it.message}", testResponse = null) }
            }
        }
    }

    /** Подключается: сохраняет, запускает опрос и помечает isConnected */
    fun connect() {
        val cfg = state.value.toPortConfig()
        saveConnectionSettings()
        startPollingUseCase.invoke(cfg)
        update { copy(error = null, isConnected = true) }
    }

    /** Отключается: останавливает опрос и сбрасывает isConnected */
    fun disconnect() {
        stopPollingUseCase.invoke()
        update { copy(isConnected = false) }
    }

    // Сеттеры для UI-поля
    fun selectPort(key: String)       = update { copy(selectedPort   = key) }
    fun setSlaveAddress(v: String)    = update { copy(slaveAddress   = v) }
    fun setBaudRate(v: String)        = update { copy(baudRate       = v) }
    fun setDataBits(v: String)        = update { copy(dataBits       = v) }
    fun setStopBits(v: String)        = update { copy(stopBits       = v) }
    fun setParity(v: String)          = update { copy(parity         = v) }
    fun setAttemptCount(v: String)    = update { copy(attemptCount   = v) }
    fun setTimeoutRead(v: String)     = update { copy(timeoutRead    = v) }
    fun setTimeoutWrite(v: String)    = update { copy(timeoutWrite   = v) }
}
