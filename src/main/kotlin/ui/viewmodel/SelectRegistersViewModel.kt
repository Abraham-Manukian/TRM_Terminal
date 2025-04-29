package ui.viewmodel

import domain.model.Register
import state.FilterRange
import state.RegisterSelectionUiState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue

/**
 * ViewModel для экрана выбора регистров
 */
class SelectRegistersViewModel {
    // Внутренний mutableStateOf для состояния UI
    private val _uiState = mutableStateOf(RegisterSelectionUiState())
    
    // Публичное свойство для наблюдения состояния UI
    val uiState: RegisterSelectionUiState by _uiState
    
    // Список всех доступных регистров (для обратной совместимости)
    val availableRegisters: List<Register> get() = uiState.allRegisters
    
    init {
        // Инициализация списка регистров
        loadRegisters()
    }
    
    /**
     * Загрузка списка доступных регистров
     */
    private fun loadRegisters() {
        // Пример регистров для демонстрации
        val sampleRegisters = listOf(
            Register(6, "Регистр статуса", "Состояние устройства"),
            Register(7, "Регистр управления", "Управление устройством"),
            Register(8, "Температура", "Текущая температура"),
            Register(9, "Давление", "Текущее давление"),
            Register(10, "Влажность", "Текущая влажность"),
            Register(11, "Скорость", "Текущая скорость"),
            Register(57, "Настройка 1", "Параметр настройки 1"),
            Register(58, "Настройка 2", "Параметр настройки 2"),
            Register(59, "Настройка 3", "Параметр настройки 3"),
            Register(85, "Статус ошибки", "Код ошибки"),
            Register(86, "Режим работы", "Текущий режим"),
            Register(87, "Конфигурация", "Параметр конфигурации")
        )
        
        updateState(
            _uiState.value.copy(
                allRegisters = sampleRegisters,
                isLoading = false
            )
        )
    }
    
    /**
     * Переключение выбора регистра
     */
    fun toggleRegisterSelection(register: Register) {
        val selectedIds = _uiState.value.selectedRegisterIds.toMutableSet()
        
        if (selectedIds.contains(register.address)) {
            selectedIds.remove(register.address)
        } else {
            selectedIds.add(register.address)
        }
        
        updateState(_uiState.value.copy(selectedRegisterIds = selectedIds))
    }
    
    /**
     * Сохранение выбранных регистров
     */
    fun saveSelectedRegisters() {
        // Здесь будет логика сохранения выбранных регистров
        // Например, сохранение в базу данных или в настройки
        
        // Для демонстрации просто выводим сообщение
        println("Сохранено ${_uiState.value.selectedCount} регистров")
    }
    
    /**
     * Обновление поискового запроса
     */
    fun updateSearchQuery(query: String) {
        updateState(_uiState.value.copy(searchQuery = query))
    }
    
    /**
     * Обновление фильтра диапазона
     */
    fun updateFilterRange(range: FilterRange) {
        updateState(_uiState.value.copy(filterRange = range))
    }
    
    /**
     * Переключение показа диалога фильтра
     */
    fun toggleFilterDialog(show: Boolean) {
        updateState(_uiState.value.copy(showFilterDialog = show))
    }
    
    /**
     * Проверка выбран ли регистр (для обратной совместимости)
     */
    fun isSelected(register: Register): Boolean {
        return _uiState.value.isSelected(register)
    }
    
    /**
     * Обновление внутреннего состояния
     */
    private fun updateState(newState: RegisterSelectionUiState) {
        _uiState.value = newState
    }
} 