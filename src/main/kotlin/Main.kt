package org.example

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import theme.AppTheme
import ui.ConnectionScreen
import ui.MainMenuScreen
import ui.RequestScreen
import viewmodel.MainViewModel

fun main() = application {
    val viewModel = remember { MainViewModel() }
    val isDarkTheme = remember { mutableStateOf(false) }
    val currentScreen = remember { mutableStateOf(AppScreen.MainMenu) }

    AppTheme(isDark = isDarkTheme.value) {
        Window(onCloseRequest = ::exitApplication, title = "TRM1 Terminal") {
            when (currentScreen.value) {
                AppScreen.MainMenu -> MainMenuScreen(
                    onNavigate = { currentScreen.value = it },
                    isDark = isDarkTheme.value,
                    onToggleTheme = { isDarkTheme.value = !isDarkTheme.value }
                )
                AppScreen.Connection -> ConnectionScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen.value = AppScreen.MainMenu }
                )
                AppScreen.Request -> RequestScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen.value = AppScreen.MainMenu }
                )
            }
        }
    }
}