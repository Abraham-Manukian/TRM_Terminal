package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ui.components.DropdownMenuField
import ui.viewmodel.RequestViewModel
import state.DisplayMode
import state.ByteOrder


class RequestScreen(
    private val viewModel: RequestViewModel
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
                IconButton(onClick = { navigator?.pop() }) {
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
                            viewModel.updateState(state.copy(displayMode = mode))
                        }
                    ) {
                        RadioButton(selected = selected, onClick = null)
                        Text(
                            text = mode.label,
                            modifier = Modifier.padding(start = 4.dp),
                            color = colors.onBackground
                        )
                    }
                }
            }

            DropdownMenuField(
                label = "Формат",
                selected = state.byteOrder.label,
                options = ByteOrder.values().map { it.label }
            ) { label ->
                val order = ByteOrder.values().find { it.label == label } ?: ByteOrder.ABCD
                viewModel.updateState(state.copy(byteOrder = order))
            }

            Button(onClick = { viewModel.sendGeneratedRequest() }, modifier = Modifier.fillMaxWidth()) {
                Text("Отправить команду")
            }

            Divider(color = colors.onSurface.copy(alpha = 0.3f))

            Text("Ручной запрос (Hex)", color = colors.onBackground)

            OutlinedTextField(
                value = state.rawRequest,
                onValueChange = { viewModel.updateState(state.copy(rawRequest = it)) },
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
            
            state.error?.let {
                Text(it, color = colors.error, style = MaterialTheme.typography.body2)
            }
        }
    }
}