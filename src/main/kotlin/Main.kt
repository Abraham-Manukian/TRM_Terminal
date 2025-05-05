package org.example

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import di.appModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import theme.AppTheme
import ui.screen.MainMenuScreen
import ui.viewmodel.MainMenuViewModel

fun main() = application {
    KoinApplication(application = {
        modules(appModule)
    }) {
        val viewModel = koinInject<MainMenuViewModel>()
        AppTheme(isDark = viewModel.isDarkTheme) {
            Window(onCloseRequest = ::exitApplication, title = "TRM1 Terminal") {
                Navigator(screen = MainMenuScreen())
            }
        }
    }
}