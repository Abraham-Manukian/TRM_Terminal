package org.example

import org.example.data.DevicePoller
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import di.appModule
import di.domainModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import theme.AppTheme
import ui.screen.MainMenuScreen
import ui.viewmodel.MainMenuViewModel

fun main() = application {
    KoinApplication(application = {
        modules(appModule, domainModule)
    }) {
        val viewModel = koinInject<MainMenuViewModel>()
        val themeState = viewModel.state.collectAsState()
        AppTheme(isDark = themeState.value.isDarkTheme) {
            Window(onCloseRequest = ::exitApplication, title = "TRM1 Terminal") {
                //DevicePoller
                Navigator(screen = MainMenuScreen())
            }
        }
    }
}