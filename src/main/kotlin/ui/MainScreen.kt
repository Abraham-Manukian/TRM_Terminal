package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import state.DisplayMode
import ui.components.DropdownMenuField
import viewmodel.MainViewModel

@Composable
fun MainScreen() {
    val viewModel = remember { MainViewModel() }
    val state = viewModel.state

    val slaveInt = state.slaveAddress.toIntOrNull()
    val isSlaveValid = slaveInt != null && slaveInt in 1..247

    LaunchedEffect(Unit) {
        viewModel.loadPorts()
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("TRM1 Terminal", style = MaterialTheme.typography.h5)

            DropdownMenuField("COM Port", state.selectedPort, state.ports) {
                viewModel.update { copy(selectedPort = it) }
            }

            OutlinedTextField(
                value = state.slaveAddress,
                onValueChange = {
                    if (it.all { ch -> ch.isDigit() } && (it.toIntOrNull() ?: 0) <= 999) {
                        viewModel.update { copy(slaveAddress = it) }
                    }
                },
                label = { Text("Slave Address (1–247)") },
                isError = !isSlaveValid,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (!isSlaveValid) {
                Text(
                    text = "Введите число от 1 до 247",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption
                )
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
            DropdownMenuField("Request Type", state.requestType, listOf("Read Holding Registers", "Write Single Register")) {
                viewModel.update { copy(requestType = it) }
            }

            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.update { copy(address = it) } },
                label = { Text("Start Address") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.quantity,
                onValueChange = { viewModel.update { copy(quantity = it) } },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Формат ответа:", style = MaterialTheme.typography.subtitle1)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                DisplayMode.values().forEach { mode ->
                    val selected = state.displayMode == mode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.update { copy(displayMode = mode) }
                        }
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = null
                        )
                        Text(
                            text = mode.label,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.sendGeneratedRequest() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отправить команду")
            }

            Divider()
            Text("Ручной запрос (Hex)")
            OutlinedTextField(
                value = state.rawRequest,
                onValueChange = { viewModel.update { copy(rawRequest = it) } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Hex-запрос, например: 01 03 00 00 00 02") }
            )
            Button(onClick = { viewModel.sendRawRequest() }, modifier = Modifier.fillMaxWidth()) {
                Text("Отправить ручной запрос")
            }

            Divider()
            Text("Отправленный запрос:", style = MaterialTheme.typography.subtitle1)
            OutlinedTextField(
                value = state.lastRequestHex,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                label = { Text("Hex-запрос, отправленный в устройство") }
            )

            OutlinedTextField(
                value = viewModel.getFormattedResponse(),
                onValueChange = {},
                label = { Text("Ответ (${state.displayMode.name})") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                readOnly = true
            )
        }
    }
}
