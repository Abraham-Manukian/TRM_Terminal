package ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import state.ThemeState

class MainMenuViewModel : KoinComponent {
    var themeState by mutableStateOf(ThemeState())
        private set
        
    fun toggleTheme() {
        themeState = themeState.copy(isDarkTheme = !themeState.isDarkTheme)
    }
} 