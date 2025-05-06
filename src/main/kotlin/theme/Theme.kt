package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

@Composable
fun AppTheme(isDark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isDark) DarkColorPalette else LightColorPalette
    ) {
        content()
    }
}
