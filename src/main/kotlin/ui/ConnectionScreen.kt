package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.DropdownMenuField
import ui.components.NotificationManager
import ui.components.NotificationPopup
import viewmodel.MainViewModel

@Composable
fun ConnectionScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val state = viewModel.state
    val colors = MaterialTheme.colors
    val tfColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = colors.onBackground,
        cursorColor = colors.primary,
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
        focusedLabelColor = colors.primary,
        unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f),
        placeholderColor = colors.onSurface.copy(alpha = 0.3f)
    )

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.showAllPorts){
        viewModel.loadPorts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val valid = state.slaveAddress.toIntOrNull() in 1..247
                if (valid) onBack()
                else NotificationManager.show("Сначала введите корректный адрес")
            }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = colors.onBackground)
            }
            Text("Настройки подключения", style = MaterialTheme.typography.h6, color = colors.onBackground)
        }

        DropdownMenuField(
            label = "COM Port",
            selected = state.ports[state.selectedPort] ?: "",
            options = state.ports.values.toList()
        ) { selectedText ->
            val selectedKey = state.ports.entries.find { it.value == selectedText }?.key ?: ""
            viewModel.update { copy(selectedPort = selectedKey) }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Checkbox(
                checked = state.showAllPorts,
                onCheckedChange = {
                    viewModel.update { copy(showAllPorts = it) }
                    viewModel.loadPorts()
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = colors.primary,
                    uncheckedColor = colors.onBackground
                )
            )
            Text("Показать все COM порты", color = colors.onBackground)
        }

        DropdownMenuField("Baud Rate", state.baudRate, listOf("9600", "19200", "38400")) {
            viewModel.update { copy(baudRate = it) }
        }

        DropdownMenuField("Data Bits", state.dataBits, listOf("7", "8")) {
            viewModel.update { copy(dataBits = it) }
        }

        DropdownMenuField("Stop Bits", state.stopBits, listOf("1", "2")) {
            viewModel.update { copy(stopBits = it) }
        }

        OutlinedTextField(
            value = state.slaveAddress,
            onValueChange = {
                if (it.all { c -> c.isDigit() } && (it.toIntOrNull() ?: 0) <= 999)
                    viewModel.update { copy(slaveAddress = it) }
            },
            label = { Text("Slave Address (1–247)") },
            singleLine = true,
            isError = state.slaveAddress.toIntOrNull() !in 1..247,
            modifier = Modifier.fillMaxWidth(),
            colors = tfColors
        )

        if (state.slaveAddress.toIntOrNull() !in 1..247) {
            Text("Введите число от 1 до 247", color = colors.error, style = MaterialTheme.typography.caption)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.saveConnectionSettings() },
            modifier = Modifier.fillMaxWidth()
            )
        {
            Text("Сохранить")
        }
    }
    NotificationPopup()
}
