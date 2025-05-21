package ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import domain.model.Register
import org.example.domain.repository.PollingService
import domain.usecase.StartPollingUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import state.FilterRange
import state.RegisterSelectionUiState

class SelectRegistersViewModel(
    val connectionViewModel: ConnectionViewModel,
    private val pollingService: PollingService,
    private val startPollingUseCase: StartPollingUseCase
) : ScreenModel {
    private val _uiState = MutableStateFlow(RegisterSelectionUiState(isLoading = true))
    val uiState: StateFlow<RegisterSelectionUiState> = _uiState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val fieldJobs = mutableMapOf<Int, Job>()

    init {
        connectionViewModel.state
            .map { it.toPortConfig() }
            .distinctUntilChanged()
            .onEach { cfg ->
                pollingService.startPolling(cfg)
                // ← вот новый код
                val regs = pollingService.getRegisters()
                _uiState.update { it.copy(
                    allRegisters = regs,
                    isConnected  = true,
                    isLoading    = false
                ) }
            }
            .launchIn(scope)
    }

    private fun startFieldPolling(address: Int) {
        fieldJobs[address]?.cancel()
        fieldJobs[address] = scope.launch {
            while (isActive) {
                pollingService.startFieldPolling(address) { v ->
                    _uiState.update { st ->
                        st.copy(currentValues = st.currentValues + (address to v))
                    }
                }
                delay(connectionViewModel.state.value.pollIntervalMillis)
            }
        }
    }

    fun toggleRegisterSelection(reg: Register) {
        val addr = reg.address
        val sel = _uiState.value.selectedRegisterIds.toMutableSet()
        if (sel.remove(addr)) {
            fieldJobs.remove(addr)?.cancel()
        } else {
            sel.add(addr)
            startFieldPolling(addr)
        }
        _uiState.update { it.copy(selectedRegisterIds = sel) }
    }

    fun updateSearchQuery(q: String)      = _uiState.update { it.copy(searchQuery = q) }
    fun updateFilterRange(r: FilterRange) = _uiState.update { it.copy(filterRange = r) }
    fun toggleFilterDialog(s: Boolean)    = _uiState.update { it.copy(showFilterDialog = s) }

    fun saveSelectedRegisters() {
        // ваша логика
    }

    fun writeRegister(reg: Register, v: Double) {
        scope.launch { pollingService.writeRegister(reg.address, v) }
    }
}
