package state

import domain.model.Register

/**
 * Состояние UI для экрана выбора регистров
 */
data class RegisterSelectionUiState(
    // Список всех доступных регистров
    val allRegisters: List<Register> = emptyList(),
    
    // Список выбранных регистров (их идентификаторы или адреса)
    val selectedRegisterIds: Set<Int> = emptySet(),
    
    // Строка поиска
    val searchQuery: String = "",
    
    // Выбранный диапазон фильтрации
    val filterRange: FilterRange = FilterRange.ALL,
    
    // Флаг загрузки данных
    val isLoading: Boolean = false,
    
    // Сообщение об ошибке (если есть)
    val errorMessage: String? = null,
    
    // Флаг показа диалога фильтра
    val showFilterDialog: Boolean = false
) {
    /**
     * Отфильтрованный список регистров на основе поискового запроса и фильтра диапазона
     */
    val filteredRegisters: List<Register> get() {
        if (allRegisters.isEmpty()) return emptyList()
        
        return allRegisters.filter { register ->
            // Фильтр по поисковому запросу
            val matchesSearch = searchQuery.isEmpty() || 
                register.name.contains(searchQuery, ignoreCase = true) || 
                register.address.toString().contains(searchQuery)
            
            // Фильтр по диапазону
            val matchesRange = when (filterRange) {
                FilterRange.ALL -> true
                FilterRange.RANGE_6_11 -> register.address in 6..11
                FilterRange.RANGE_57_87 -> register.address in 57..87
            }
            
            matchesSearch && matchesRange
        }
    }
    
    /**
     * Проверка выбран ли регистр
     */
    fun isSelected(register: Register): Boolean {
        return selectedRegisterIds.contains(register.address)
    }
    
    /**
     * Возвращает количество выбранных регистров
     */
    val selectedCount: Int get() = selectedRegisterIds.size
} 