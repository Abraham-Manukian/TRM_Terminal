package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.koin.compose.koinInject
import ui.viewmodel.MainMenuViewModel

@Composable
fun AppTheme(isDark: MutableState<Boolean> = mutableStateOf(false), content: @Composable () -> Unit) {
    MaterialTheme(colors = if (isDark.value) DarkColorPalette else LightColorPalette) {
        println("111111111111111111")
        content()
    }
}
