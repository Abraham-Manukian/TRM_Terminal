package ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import domain.model.PortInfo
import domain.usecase.LoadPortsUseCase
import domain.usecase.SaveConnectionSettingsUseCase
import domain.usecase.SendRequestUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import state.ConnectionState
import ui.components.NotificationManager

/**
 * ViewModel для экрана настроек подключения
 */
class ConnectionViewModel(
    private val loadPortsUseCase: LoadPortsUseCase,
    private val saveConnectionSettingsUseCase: SaveConnectionSettingsUseCase,
    private val sendRequestUseCase: SendRequestUseCase
) : KoinComponent {
    // Состояние представления с использованием mutableStateOf для реактивности
    private val _state = mutableStateOf(ConnectionState())
    val state: ConnectionState by _state
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    init {
        loadPorts()
    }
    
    /**
     * Обновление состояния
     */
    fun updateState(newState: ConnectionState) {
        _state.value = newState
    }
    
    /**
     * Загрузка доступных портов
     */
    fun loadPorts() {
        // Используем LoadPortsUseCase для получения списка портов
        val ports = loadPortsUseCase.execute(state.showAllPorts)
        
        updateState(state.copy(
            ports = ports.associateBy { it.systemName }
        ))
    }
    
    /**
     * Переключение опции показа всех портов
     */
    fun toggleShowAllPorts() {
        updateState(state.copy(showAllPorts = !state.showAllPorts))
        loadPorts()
    }
    
    /**
     * Сохранение настроек подключения
     */
    fun saveConnectionSettings() {
        // Здесь будет логика сохранения настроек с использованием saveConnectionSettingsUseCase
        saveConnectionSettingsUseCase.execute(state.slaveAddress)
    }
    
    /**
     * Отправка тестового запроса
     */
    fun sendTestRequest() {
        // Здесь будет логика отправки тестового запроса с использованием sendRequestUseCase
        updateState(state.copy(
            testResponse = "Соединение успешно установлено",
            error = null
        ))
    }
} 