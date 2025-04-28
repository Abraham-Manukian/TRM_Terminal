package org.example

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import theme.AppTheme
import ui.ConnectionScreen
import ui.MainMenuScreen
import ui.RequestScreen
import viewmodel.MainViewModel

fun main() = application {
    val viewModel = remember { MainViewModel() }
    val isDarkTheme = remember { mutableStateOf(false) }

    AppTheme(isDark = isDarkTheme.value) {
        Window(onCloseRequest = ::exitApplication, title = "TRM1 Terminal") {
            Navigator(screen = MainMenuScreen(viewModel, isDarkTheme))
        }
    }
}