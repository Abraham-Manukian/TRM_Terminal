package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.components.DropdownMenuField
import ui.components.NotificationPopup
import ui.viewmodel.RequestViewModel
import state.DisplayMode
import state.ByteOrder
import state.RequestState
import java.lang.Thread.sleep
import androidx.compose.runtime.collectAsState


class RequestScreen() : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val colors = MaterialTheme.colors
        val tfColors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = colors.onBackground,
            cursorColor = colors.primary,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f),
            backgroundColor = colors.surface.copy(alpha = 0.1f)
        )
        val viewModel = koinInject<RequestViewModel>()
        val state by viewModel.state.collectAsState()
        val displayModes = DisplayMode.values().toList()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок с улучшенным визуальным стилем
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                backgroundColor = colors.primary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigator?.pop() },
                        modifier = Modifier
                            .background(
                                color = colors.surface.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = colors.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        tint = colors.onPrimary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "Окно запроса",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.onPrimary
                    )
                }
            }

            // Блок формата ответа
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Формат ответа",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.primary
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(displayModes) { mode ->
                            val isSelected = state.displayMode == mode

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) colors.primary.copy(alpha = 0.1f) else Color.Transparent
                                    )
                                    .clickable {
                                        viewModel.setDisplayMode(mode)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.setDisplayMode(mode) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colors.primary
                                    )
                                )
                                Text(
                                    text = "${mode.name} (${getModeDescription(mode)})",
                                    modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                                    color = if (isSelected) colors.primary else colors.onBackground,
                                    style = MaterialTheme.typography.body2.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }

                    // Выпадающий список для выбора порядка байтов
                    DropdownMenuField(
                        label = "Порядок байтов",
                        selected = state.byteOrder.label,
                        options = ByteOrder.values().map { it.label },
                        onSelected = viewModel::setByteOrder
                    )

                    // Информация о порядке байтов
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        backgroundColor = colors.surface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        elevation = 0.dp
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                "Подсказка: для устройств, которые показывают 0 вместо реальных значений, попробуйте другой порядок байтов.",
                                style = MaterialTheme.typography.caption,
                                color = colors.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.toggleAutoRequest() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colors.primary
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            if (state.isAutoRequestRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (state.isAutoRequestRunning) "Стоп" else "Старт",
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Ручной запрос
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Ручной запрос (Hex)",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.primary
                    )

                    OutlinedTextField(
                        value = state.rawRequest,
                        onValueChange = { viewModel.setRawRequest(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hex-запрос") },
                        colors = tfColors,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedButton(
                        onClick = { viewModel.sendRawRequest() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        Text(
                            "Отправить ручной запрос",
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }

            // Результаты запроса
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Результаты",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.primary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surface.copy(alpha = 0.15f))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Отправленный запрос:",
                            style = MaterialTheme.typography.caption,
                            color = colors.primary.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            state.lastRequestHex,
                            style = MaterialTheme.typography.body2,
                            color = colors.onSurface
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surface.copy(alpha = 0.15f))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Ответ (${state.displayMode.name}):",
                            style = MaterialTheme.typography.caption,
                            color = colors.primary.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            viewModel.getFormattedResponse(),
                            style = MaterialTheme.typography.body2,
                            color = colors.onSurface
                        )
                    }
                }
            }

            // Блок с ошибками
            state.error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = colors.error.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Ошибка:",
                            color = colors.error,
                            style = MaterialTheme.typography.subtitle2.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMessage,
                            color = colors.error,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }

        // Показ уведомлений
        NotificationPopup()
    }

    // Добавим функцию для получения описания режима
    fun getModeDescription(mode: DisplayMode): String {
        return when (mode) {
            DisplayMode.DEC -> "Десятичный"
            DisplayMode.HEX -> "Шестнадцатеричный"
            DisplayMode.FLOAT -> "Плавающая точка"
        }
    }
}