package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import state.DisplayMode
import viewmodel.MainViewModel

@Composable
fun RequestScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val state = viewModel.state
    val colors = MaterialTheme.colors
    val tfColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = colors.onBackground,
        cursorColor = colors.primary,
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
        focusedLabelColor = colors.primary,
        unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = colors.onBackground)
            }
            Text("Окно запроса", style = MaterialTheme.typography.h6, color = colors.onBackground)
        }

        Text("Формат ответа:", style = MaterialTheme.typography.subtitle1, color = colors.onBackground)

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
                    RadioButton(selected = selected, onClick = null)
                    Text(text = mode.label, modifier = Modifier.padding(start = 4.dp), color = colors.onBackground)
                }
            }
        }

        Button(onClick = { viewModel.sendGeneratedRequest() }, modifier = Modifier.fillMaxWidth()) {
            Text("Отправить команду")
        }

        Divider(color = colors.onSurface.copy(alpha = 0.3f))

        Text("Ручной запрос (Hex)", color = colors.onBackground)

        OutlinedTextField(
            value = state.rawRequest,
            onValueChange = { viewModel.update { copy(rawRequest = it) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Hex-запрос") },
            colors = tfColors
        )

        Button(onClick = { viewModel.sendRawRequest() }, modifier = Modifier.fillMaxWidth()) {
            Text("Отправить ручной запрос")
        }

        Divider(color = colors.onSurface.copy(alpha = 0.3f))

        Text("Отправленный запрос:", style = MaterialTheme.typography.subtitle1, color = colors.onBackground)

        OutlinedTextField(
            value = state.lastRequestHex,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().height(80.dp),
            label = { Text("Hex-запрос, отправленный в устройство") },
            colors = tfColors
        )

        OutlinedTextField(
            value = viewModel.getFormattedResponse(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            label = { Text("Ответ (${state.displayMode.name})") },
            colors = tfColors
        )
    }
}
