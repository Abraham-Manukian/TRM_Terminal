package ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ui.viewmodel.SelectRegistersViewModel
import domain.model.Register

class PinnedRegistersScreen(
    private val viewModel: SelectRegistersViewModel
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val state by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Прикреплённые регистры") },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.allRegisters
                    .filter { state.isSelected(it) }
                    .forEach { register ->
                        // локальное состояние ввода
                        var text by remember { mutableStateOf(
                            state.currentValues[register.address]?.toString() ?: ""
                        )}
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                register.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.body1
                            )
                            TextField(
                                value = text,
                                onValueChange = { newText ->
                                    text = newText
                                    newText.toDoubleOrNull()?.let { newVal ->
                                        viewModel.writeRegister(register, newVal)
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
            }
        }
    }
}
