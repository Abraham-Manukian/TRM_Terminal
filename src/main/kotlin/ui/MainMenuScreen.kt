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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import viewmodel.MainViewModel

class MainMenuScreen (
    private val viewModel: MainViewModel,
    private val isDarkTheme: MutableState<Boolean>
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current


        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TRM1 Terminal", style = MaterialTheme.typography.h5, color = MaterialTheme.colors.onBackground)
                Button(onClick = {
                    navigator?.push(ConnectionScreen(viewModel))
                }) {
                    Text("Настройки подключения")
                }
                Button(onClick = {
                    navigator?.push(RequestScreen(viewModel))
                }) {
                    Text("Опрос данных")
                }
            }

            IconButton(
                onClick = { isDarkTheme.value = !isDarkTheme.value },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(
                    imageVector = if (isDarkTheme.value) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Toggle theme"
                )
            }
        }
    }
}
