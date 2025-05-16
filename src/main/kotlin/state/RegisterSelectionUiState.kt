package state

import domain.model.Register

data class RegisterSelectionUiState(
    val allRegisters: List<Register> = emptyList(),
    val selectedRegisterIds: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val filterRange: FilterRange = FilterRange.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showFilterDialog: Boolean = false,
    val isConnected: Boolean = false,
    val currentValues: Map<Int, Double> = emptyMap()
) {
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

    fun isSelected(register: Register): Boolean =
        selectedRegisterIds.contains(register.address)

    val selectedCount: Int
        get() = selectedRegisterIds.size
}
