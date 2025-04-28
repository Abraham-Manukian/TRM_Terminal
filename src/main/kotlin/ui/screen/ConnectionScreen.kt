package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ui.components.DropdownMenuField
import ui.components.NotificationManager
import ui.components.NotificationPopup
import ui.viewmodel.ConnectionViewModel
import state.ConnectionState

class ConnectionScreen(
    private val viewModel: ConnectionViewModel
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
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

        LaunchedEffect(state.showAllPorts) {
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
                    if (state.slaveAddress.toIntOrNull() in 1..247) {
                        navigator?.pop()
                    } else {
                        NotificationManager.show("Сначала введите корректный адрес")
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = colors.onBackground)
                }
                Text("Настройки подключения", style = MaterialTheme.typography.h6, color = colors.onBackground)
            }

            DropdownMenuField(
                label = "COM Port",
                selected = state.ports[state.selectedPort]?.displayName ?: "",
                options = state.ports.values.map { it.displayName }
            ) { selectedText ->
                val selectedKey = state.ports.entries.find { it.value.displayName == selectedText }?.key ?: ""
                viewModel.updateState(state.copy(selectedPort = selectedKey))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Checkbox(
                    checked = state.showAllPorts,
                    onCheckedChange = {
                        viewModel.toggleShowAllPorts()
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.primary,
                        uncheckedColor = colors.onBackground
                    )
                )
                Text("Показать все COM порты", color = colors.onBackground)
            }

            DropdownMenuField("Baud Rate", state.baudRate, listOf("9600", "19200", "38400")) {
                viewModel.updateState(state.copy(baudRate = it))
            }

            DropdownMenuField("Data Bits", state.dataBits, listOf("7", "8")) {
                viewModel.updateState(state.copy(dataBits = it))
            }

            DropdownMenuField("Stop Bits", state.stopBits, listOf("1", "2")) {
                viewModel.updateState(state.copy(stopBits = it))
            }

            OutlinedTextField(
                value = state.slaveAddress,
                onValueChange = {
                    if (it.all { c -> c.isDigit() } && (it.toIntOrNull() ?: 0) <= 999)
                        viewModel.updateState(state.copy(slaveAddress = it))
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
            ) {
                Text("Сохранить")
            }
            
            Button(
                onClick = { viewModel.sendTestRequest() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Проверить соединение")
            }
            
            state.testResponse?.let {
                Text(it, color = colors.primary, style = MaterialTheme.typography.body2)
            }
            
            state.error?.let {
                Text(it, color = colors.error, style = MaterialTheme.typography.body2)
            }
        }

        NotificationPopup()
    }
}