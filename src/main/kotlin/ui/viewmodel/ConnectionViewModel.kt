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
import state.ConnectionState
import ui.components.NotificationManager

class ConnectionViewModel(
    private val loadPortsUseCase: LoadPortsUseCase,
    private val saveConnectionSettingsUseCase: SaveConnectionSettingsUseCase,
    private val sendRequestUseCase: SendRequestUseCase
) {
    var state by mutableStateOf(ConnectionState())
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
    
    fun updateState(newState: ConnectionState) {
        state = newState
    }
    
    fun saveConnectionSettings() {
        saveConnectionSettingsUseCase.execute(state.slaveAddress)
    }
    
    fun sendTestRequest() {
        scope.launch {
            try {
                val request = byteArrayOf(0x01, 0x03, 0x00, 0x01, 0x00, 0x01, 0x0A, 0x0B)
                val response = sendRequestUseCase.sendRequest(
                    portName = state.selectedPort,
                    baudRate = state.baudRate.toInt(),
                    dataBits = state.dataBits.toInt(),
                    stopBits = state.stopBits.toInt(),
                    request = request
                )
                state = state.copy(testResponse = "Успешно: ${response.size} байт получено", error = null)
            } catch (e: Exception) {
                state = state.copy(error = "Ошибка: ${e.message}", testResponse = null)
            }
        }
    }
} 