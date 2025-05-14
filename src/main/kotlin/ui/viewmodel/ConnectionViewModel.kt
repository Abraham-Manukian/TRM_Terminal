package ui.viewmodel


import cafe.adriel.voyager.core.model.ScreenModel
import domain.usecase.LoadPortsUseCase
import domain.usecase.SaveConnectionSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import state.ConnectionState

class ConnectionViewModel(
    private val loadPortsUseCase: LoadPortsUseCase,
    private val saveConnectionSettingsUseCase: SaveConnectionSettingsUseCase,
) : ScreenModel {

    private val _state = MutableStateFlow(ConnectionState())
    val state: StateFlow<ConnectionState> = _state

    private val currentState: ConnectionState get() = state.value

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    init {
        loadPorts()
    }

    private fun updateState(update: (ConnectionState) -> ConnectionState) {
        _state.update(update)
    }

    fun loadPorts() {
        viewModelScope.launch {
            try {
                val ports = loadPortsUseCase.execute(currentState.showAllPorts)
                
                updateState { state ->
                    state.copy(
                        ports = ports.associateBy { it.systemName }
                    )
                }
            } catch (e: Exception) {
                updateState { state ->
                    state.copy(
                        error = "Ошибка загрузки портов: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleShowAllPorts() {
        updateState { state ->
            state.copy(showAllPorts = !state.showAllPorts)
        }
        loadPorts()
    }

    fun selectPort(portKey: String) {
        updateState { state ->
            state.copy(selectedPort = portKey)
        }
    }

    fun setBaudRate(baudRate: String) {
        updateState { state ->
            state.copy(baudRate = baudRate)
        }
    }

    fun setSlaveAddress(address: String) {
        updateState { state ->
            state.copy(slaveAddress = address)
        }
    }

    fun setDataBits(dataBits: String) {
        updateState { state ->
            state.copy(dataBits = dataBits)
        }
    }

    fun setStopBits(stopBits: String) {
        updateState { state ->
            state.copy(stopBits = stopBits)
        }
    }
    fun setParity(parity: String)         = updateState { it.copy(parity = parity) }
    fun setAttemptCount(attemptCount: String)   = updateState { it.copy(attemptCount = attemptCount) }
    fun setTimeoutRead(timeoutRead: String)    = updateState { it.copy(timeoutRead = timeoutRead) }
    fun setTimeoutWrite(timeoutWrite: String)   = updateState { it.copy(timeoutWrite = timeoutWrite) }

    fun saveConnectionSettings() {
        viewModelScope.launch {
            try {
                // вместо старого saveConnectionSettingsUseCase(slaveAddress)
                val cfg = currentState.toPortConfig()
                saveConnectionSettingsUseCase.invoke(cfg)
                updateState { it.copy(error = null) }
            } catch (e: Exception) {
                updateState { it.copy(error = "Ошибка сохранения: ${e.message}") }
            }
        }
    }

    fun sendTestRequest() {
        viewModelScope.launch {
            try {
                val result = "Соединение успешно установлено" // Заглушка, заменить на результат от useCase
                
                updateState { state ->
                    state.copy(
                        testResponse = result,
                        error = null
                    )
                }
            } catch (e: Exception) {
                updateState { state ->
                    state.copy(
                        error = "Ошибка при тестировании соединения: ${e.message}"
                    )
                }
            }
        }
    }
} 