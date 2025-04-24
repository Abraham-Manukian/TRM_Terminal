package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.AppScreen

@Composable
fun MainMenuScreen(
    onNavigate: (AppScreen) -> Unit,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TRM1 Terminal", style = MaterialTheme.typography.h4, color = MaterialTheme.colors.onBackground)
            Button(onClick = { onNavigate(AppScreen.Connection) }) {
                Text("Настройки подключения")
            }
            Button(onClick = { onNavigate(AppScreen.Request) }) {
                Text("Окно запроса")
            }
        }

        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(
                imageVector = if (isDark) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                contentDescription = "Toggle theme",
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}
