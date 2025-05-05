package ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import domain.usecase.LoadPortsUseCase
import domain.usecase.SaveConnectionSettingsUseCase
import domain.usecase.SendRawRequestUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import state.ConnectionState

/**
 * ViewModel для экрана настроек подключения
 * Реализует ScreenModel из Voyager и использует StateFlow для управления состоянием
 */
class ConnectionViewModel(
    private val loadPortsUseCase: LoadPortsUseCase,
    private val saveConnectionSettingsUseCase: SaveConnectionSettingsUseCase,
    private val sendRequestUseCase: SendRawRequestUseCase
) : ScreenModel, KoinComponent {
    
    // Приватный StateFlow для внутреннего изменения состояния
    private val _state = MutableStateFlow(ConnectionState())
    
    // Публичный неизменяемый StateFlow для наблюдения за состоянием
    val state: StateFlow<ConnectionState> = _state.asStateFlow()
    
    // Текущее состояние UI
    private val currentState: ConnectionState get() = state.value
    
    // Область видимости coroutine для выполнения асинхронных операций с SupervisorJob,
    // чтобы ошибка в одной корутине не отменяла другие
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    init {
        loadPorts()
    }
    
    /**
     * Обновляет текущее состояние с помощью указанного блока
     * @param update функция для обновления состояния
     */
    private fun updateState(update: (ConnectionState) -> ConnectionState) {
        _state.update(update)
    }
    
    /**
     * Загрузка доступных портов
     */
    fun loadPorts() {
        viewModelScope.launch {
            try {
                // Используем LoadPortsUseCase для получения списка портов
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
    
    /**
     * Переключение опции показа всех портов
     */
    fun toggleShowAllPorts() {
        updateState { state ->
            state.copy(showAllPorts = !state.showAllPorts)
        }
        loadPorts()
    }
    
    /**
     * Выбор COM-порта
     */
    fun selectPort(portKey: String) {
        updateState { state ->
            state.copy(selectedPort = portKey)
        }
    }
    
    /**
     * Обновление параметра скорости
     */
    fun setBaudRate(baudRate: String) {
        updateState { state ->
            state.copy(baudRate = baudRate)
        }
    }
    
    /**
     * Обновление адреса устройства
     */
    fun setSlaveAddress(address: String) {
        updateState { state ->
            state.copy(slaveAddress = address)
        }
    }
    
    /**
     * Обновление параметров бит данных
     */
    fun setDataBits(dataBits: String) {
        updateState { state ->
            state.copy(dataBits = dataBits)
        }
    }
    
    /**
     * Обновление параметров стоп-бит
     */
    fun setStopBits(stopBits: String) {
        updateState { state ->
            state.copy(stopBits = stopBits)
        }
    }
    
    /**
     * Сохранение настроек подключения
     */
    fun saveConnectionSettings() {
        viewModelScope.launch {
            try {
                saveConnectionSettingsUseCase.execute(currentState.slaveAddress)
                
                updateState { state ->
                    state.copy(error = null)
                }
            } catch (e: Exception) {
                updateState { state ->
                    state.copy(error = "Ошибка сохранения настроек: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Отправка тестового запроса
     */
    fun sendTestRequest() {
        viewModelScope.launch {
            try {
                // Логика отправки тестового запроса
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