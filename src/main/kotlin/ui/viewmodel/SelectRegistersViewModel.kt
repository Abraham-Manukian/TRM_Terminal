// src/main/kotlin/ui/viewmodel/SelectRegistersViewModel.kt
package ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import domain.model.Register
import domain.model.RegisterType
import domain.polling.PollingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import state.FilterRange
import state.RegisterSelectionUiState

class SelectRegistersViewModel(
    private val pollingService: PollingService
) : ScreenModel {
    private val _uiState = MutableStateFlow(RegisterSelectionUiState(isLoading = true))
    val uiState: StateFlow<RegisterSelectionUiState> = _uiState

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val pollingJobs = mutableMapOf<Int, Job>()

    init {
        loadRegisters()
    }

    /**
     * Инициализация списка регистров:
     *  – адреса 6–11 (дискретные/аналоговые),
     *  – адреса 57–87 (настройки, они же CONFIG, readOnly = false).
     */
    private fun loadRegisters() {
        // 6–11: первые шесть
        val regs6to11 = listOf(
            Register(6,  "Регистр статуса",    "Состояние устройства",    RegisterType.DISCRETE),
            Register(7,  "Регистр управления", "Управление устройством",  RegisterType.DISCRETE),
            Register(8,  "Температура",        "Текущая температура",     RegisterType.ANALOG),
            Register(9,  "Давление",           "Текущее давление",        RegisterType.ANALOG),
            Register(10, "Влажность",          "Текущая влажность",       RegisterType.ANALOG),
            Register(11, "Скорость",           "Текущая скорость",        RegisterType.ANALOG)
        )

        // 57–87: конфигурационные параметры
        val regs57to87 = (57..87).map { addr ->
            Register(
                address     = addr,
                name        = "Настройка $addr",
                description = "Параметр настройки $addr",
                type        = RegisterType.CONFIG,
                readOnly    = false
            )
        }

        _uiState.update {
            it.copy(
                allRegisters      = regs6to11 + regs57to87,
                isLoading         = false
            )
        }
    }

    /** Обновить поисковый запрос. */
    fun updateSearchQuery(query: String) =
        _uiState.update { it.copy(searchQuery = query) }

    /** Обновить диапазон фильтра. */
    fun updateFilterRange(range: FilterRange) =
        _uiState.update { it.copy(filterRange = range) }

    /** Показать/скрыть диалог фильтра. */
    fun toggleFilterDialog(show: Boolean) =
        _uiState.update { it.copy(showFilterDialog = show) }

    /** Выбрать все отфильтрованные регистры. */
    fun selectAll() =
        _uiState.update { st ->
            st.copy(selectedRegisterIds = st.filteredRegisters.map { it.address }.toSet())
        }

    /** Сбросить выбор регистров. */
    fun clearSelection() =
        _uiState.update { it.copy(selectedRegisterIds = emptySet()) }

    /**
     * Переключить выбор одного регистра:
     * – добавить/удалить его адрес в selectedRegisterIds,
     * – запустить/остановить real-time опрос через PollingService.
     */
    fun toggleRegisterSelection(register: Register) {
        val addr = register.address
        val newSet = _uiState.value.selectedRegisterIds.toMutableSet().apply {
            if (contains(addr)) remove(addr) else add(addr)
        }
        _uiState.update { it.copy(selectedRegisterIds = newSet) }

        if (addr in newSet) {
            // Запускаем цикличный опрос одного поля
            val job = scope.launch {
                pollingService.startFieldPolling(addr) { value ->
                    _uiState.update { st ->
                        st.copy(currentValues = st.currentValues + (addr to value))
                    }
                }
            }
            pollingJobs[addr] = job
        } else {
            // Останавливаем опрос
            pollingJobs.remove(addr)?.cancel()
        }
    }

    /** Сохранить выбранные регистры (ваша логика). */
    fun saveSelectedRegisters() {
        // TODO: сохраняем выбор куда надо (БД, SharedPreferences и т.д.)
        println("Сохранено ${_uiState.value.selectedCount} регистров")
    }

    /**
     * Записать новое значение в регистр по адресу.
     */
    fun writeRegister(register: Register, newValue: Double) {
        scope.launch {
            pollingService.writeRegister(register.address, newValue)
        }
    }
}
