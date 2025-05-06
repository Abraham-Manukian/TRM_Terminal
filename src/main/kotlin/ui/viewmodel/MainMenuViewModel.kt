package ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import state.ThemeState

class MainMenuViewModel : ScreenModel {

    private val _state = MutableStateFlow(ThemeState())
    val state: StateFlow<ThemeState> = _state

    private val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val currentState: ThemeState
        get() = _state.value

    private fun updateState(update: (ThemeState) -> ThemeState) {
        _state.update(update)
    }

    fun toggleTheme() {
        updateState { state ->
            state.copy(isDarkTheme = !state.isDarkTheme)
        }
    }
}