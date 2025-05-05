package ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainMenuViewModel : ScreenModel {

    val isDarkTheme = mutableStateOf(false)
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        
    fun toggleTheme() {
        viewModelScope.launch {
            isDarkTheme.value = !isDarkTheme.value
        }
    }
} 