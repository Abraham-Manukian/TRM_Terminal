package state

import domain.model.Register

/**
 * Состояние UI для экрана выбора регистров,
 * включая хранение актуальных значений по каждому адресу.
 */
data class RegisterSelectionUiState(
    // Список всех доступных регистров
    val allRegisters: List<Register> = emptyList(),

    // Набор адресов выбранных (прикреплённых) регистров
    val selectedRegisterIds: Set<Int> = emptySet(),

    // Строка поиска
    val searchQuery: String = "",

    // Выбранный диапазон фильтрации
    val filterRange: FilterRange = FilterRange.ALL,

    // Флаг загрузки списка регистров
    val isLoading: Boolean = false,

    // Сообщение об ошибке
    val errorMessage: String? = null,

    // Флаг показа диалога настроек фильтра
    val showFilterDialog: Boolean = false,

    // **Новое поле**: текущие значения регистров по их адресу
    val currentValues: Map<Int, Double> = emptyMap()
) {
    /**
     * Отфильтрованный список регистров по searchQuery и filterRange
     */
    val filteredRegisters: List<Register>
        get() {
            if (allRegisters.isEmpty()) return emptyList()
            return allRegisters.filter { register ->
                val matchesSearch = searchQuery.isEmpty() ||
                        register.name.contains(searchQuery, ignoreCase = true) ||
                        register.address.toString().contains(searchQuery)
                val matchesRange = when (filterRange) {
                    FilterRange.ALL       -> true
                    FilterRange.RANGE_6_11   -> register.address in 6..11
                    FilterRange.RANGE_57_87  -> register.address in 57..87
                }
                matchesSearch && matchesRange
            }
        }

    /**
     * Проверка, выбран ли регистр
     */
    fun isSelected(register: Register): Boolean =
        selectedRegisterIds.contains(register.address)

    /**
     * Количество выбранных регистров
     */
    val selectedCount: Int
        get() = selectedRegisterIds.size
}
