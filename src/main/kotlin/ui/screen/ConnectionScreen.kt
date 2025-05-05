package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import state.ConnectionState
import ui.components.DropdownMenuField
import ui.components.NotificationManager
import ui.components.NotificationPopup
import ui.viewmodel.ConnectionViewModel

class ConnectionScreen(
    private val viewModel: ConnectionViewModel
) : Screen {

    @Composable
    override fun Content() {
        // Подписываемся на state через collectAsState
        val state by viewModel.state.collectAsState()
        
        val navigator = LocalNavigator.current
        val colors = MaterialTheme.colors
        
        // Создаем улучшенные цвета для текстовых полей
        val tfColors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = colors.onBackground,
            cursorColor = colors.primary,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.onSurface.copy(alpha = 0.6f),
            placeholderColor = colors.onSurface.copy(alpha = 0.3f),
            backgroundColor = colors.surface.copy(alpha = 0.1f)
        )

        LaunchedEffect(state.showAllPorts) {
            viewModel.loadPorts()
        }

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
                        onClick = {
                            if (state.slaveAddress.toIntOrNull() in 1..247) {
                                navigator?.pop()
                            } else {
                                NotificationManager.show("Сначала введите корректный адрес")
                            }
                        },
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
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = colors.onPrimary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        "Настройки подключения",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Параметры соединения в красивой карточке
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Параметры порта",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.primary
                    )

                    DropdownMenuField(
                        label = "COM Port",
                        selected = state.ports[state.selectedPort]?.displayName ?: "",
                        options = state.ports.values.map { it.displayName }
                    ) { selectedText ->
                        val selectedKey = state.ports.entries.find { it.value.displayName == selectedText }?.key ?: ""
                        viewModel.selectPort(selectedKey)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surface.copy(alpha = 0.5f))
                            .padding(8.dp)
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
                        Text(
                            "Показать все COM порты",
                            color = colors.onBackground,
                            style = MaterialTheme.typography.body2
                        )
                    }

                    DropdownMenuField(
                        "Скорость (бит/с)",
                        state.baudRate,
                        listOf("9600", "19200", "38400")
                    ) { 
                        viewModel.setBaudRate(it)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            DropdownMenuField(
                                "Биты данных", 
                                state.dataBits, 
                                listOf("7", "8")
                            ) {
                                viewModel.setDataBits(it)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            DropdownMenuField(
                                "Стоп-биты", 
                                state.stopBits, 
                                listOf("1", "2")
                            ) {
                                viewModel.setStopBits(it)
                            }
                        }
                    }
                }
            }
            
            // Адрес устройства в отдельной карточке
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Адрес Modbus устройства",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.primary
                    )
                    
                    OutlinedTextField(
                        value = state.slaveAddress,
                        onValueChange = { 
                            if (it.all { c -> c.isDigit() } && (it.toIntOrNull() ?: 0) <= 999)
                                viewModel.setSlaveAddress(it)
                        },
                        label = { Text("Адрес (1-247)") },
                        singleLine = true,
                        isError = state.slaveAddress.toIntOrNull() !in 1..247,
                        modifier = Modifier.fillMaxWidth(),
                        colors = tfColors,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            // Кнопки с улучшенным дизайном
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveConnectionSettings() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colors.primary
                        )
                    ) {
                        Text("Сохранить")
                    }
                    
                    OutlinedButton(
                        onClick = { viewModel.sendTestRequest() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        Text("Проверить соединение")
                    }
                }
            }
            
            // Результат тестового соединения
            state.testResponse?.let { response ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp,
                    backgroundColor = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = response,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Отображение ошибки
            state.error?.let { errorText ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp,
                    backgroundColor = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorText,
                        color = Color(0xFFB71C1C),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        NotificationPopup()
    }
}