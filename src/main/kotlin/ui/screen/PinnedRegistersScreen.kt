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
import domain.model.Register
import domain.model.RegisterType
import ui.viewmodel.SelectRegistersViewModel

class PinnedRegistersScreen(
    private val viewModel: SelectRegistersViewModel
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val state     by viewModel.uiState.collectAsState()

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
                // Для каждого выбранного регистра показываем имя, текущее значение и поле для ввода
                state.allRegisters
                    .filter { state.isSelected(it) }
                    .forEach { register ->
                        val current = state.currentValues[register.address]
                        var text by remember { mutableStateOf(
                            current?.let {
                                when (register.type) {
                                    RegisterType.ANALOG -> String.format("%.2f", it)
                                    else                 -> it.toInt().toString()
                                }
                            } ?: ""
                        ) }

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                register.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.body1
                            )
                            OutlinedTextField(
                                value = text,
                                onValueChange = { new ->
                                    text = new
                                    new.toDoubleOrNull()?.let { value ->
                                        viewModel.writeRegister(register, value)
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
