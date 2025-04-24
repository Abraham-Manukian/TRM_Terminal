package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(isDark: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (isDark) DarkColorPalette else LightColorPalette
    MaterialTheme(colors = colors) {
        content()
    }
}
