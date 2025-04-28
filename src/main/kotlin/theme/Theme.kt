package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.koin.compose.koinInject
import ui.viewmodel.MainMenuViewModel

@Composable
fun AppTheme(isDark: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (isDark) DarkColorPalette else LightColorPalette
    MaterialTheme(colors = colors) {
        content()
    }
}
