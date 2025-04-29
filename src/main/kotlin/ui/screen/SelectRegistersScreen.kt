package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ui.viewmodel.SelectRegistersViewModel
import kotlinx.coroutines.launch
import state.FilterRange
import domain.model.Register

class SelectRegistersScreen(
    private val viewModel: SelectRegistersViewModel
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val colors = MaterialTheme.colors
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        
        // Получаем состояние UI
        val state = viewModel.uiState
        
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp,
                    backgroundColor = colors.primary,
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
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
                            
                            Text(
                                "Выбор регистров",
                                style = MaterialTheme.typography.h6.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.onPrimary
                            )
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            IconButton(
                                onClick = { viewModel.toggleFilterDialog(true) },
                                modifier = Modifier
                                    .background(
                                        color = colors.surface.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Фильтр",
                                    tint = colors.onPrimary
                                )
                            }
                        }
                        
                        // Строка поиска
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            placeholder = { Text("Поиск по имени или адресу") },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Поиск",
                                    tint = colors.onPrimary.copy(alpha = 0.7f)
                                ) 
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = colors.onPrimary,
                                cursorColor = colors.onPrimary,
                                leadingIconColor = colors.onPrimary,
                                focusedBorderColor = colors.onPrimary,
                                unfocusedBorderColor = colors.onPrimary.copy(alpha = 0.7f),
                                backgroundColor = colors.primary.copy(alpha = 0.3f),
                                placeholderColor = colors.onPrimary.copy(alpha = 0.7f)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = colors.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                state.filterRange.label,
                                style = MaterialTheme.typography.subtitle1.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.primary
                            )
                            
                            Text(
                                "Найдено: ${state.filteredRegisters.size}",
                                style = MaterialTheme.typography.caption,
                                color = colors.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        Divider(color = colors.onSurface.copy(alpha = 0.1f))
                        
                        if (state.filteredRegisters.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Регистры не найдены",
                                    style = MaterialTheme.typography.body1,
                                    color = colors.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.filteredRegisters) { register ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.toggleRegisterSelection(register)
                                            },
                                        elevation = 1.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        backgroundColor = if (state.isSelected(register)) 
                                            colors.primary.copy(alpha = 0.1f) 
                                        else 
                                            colors.surface
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    register.name,
                                                    style = MaterialTheme.typography.subtitle2.copy(
                                                        fontWeight = if (state.isSelected(register)) 
                                                            FontWeight.Bold 
                                                        else 
                                                            FontWeight.Normal
                                                    ),
                                                    color = colors.onSurface
                                                )
                                                
                                                Text(
                                                    "Адрес: ${register.address}",
                                                    style = MaterialTheme.typography.caption,
                                                    color = colors.onSurface.copy(alpha = 0.7f)
                                                )
                                            }
                                            
                                            Checkbox(
                                                checked = state.isSelected(register),
                                                onCheckedChange = { 
                                                    viewModel.toggleRegisterSelection(register) 
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = colors.primary,
                                                    uncheckedColor = colors.onSurface.copy(alpha = 0.6f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.saveSelectedRegisters()
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Регистры сохранены",
                                            actionLabel = "ОК"
                                        )
                                    }
                                    navigator?.pop()
                                },
                                modifier = Modifier.weight(1f),
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
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Сохранить выбор",
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    style = MaterialTheme.typography.button.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Диалог выбора фильтра
        if (state.showFilterDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleFilterDialog(false) },
                title = { Text("Выберите диапазон регистров") },
                text = {
                    Column {
                        for (range in FilterRange.values()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        viewModel.updateFilterRange(range)
                                        viewModel.toggleFilterDialog(false)
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.filterRange == range,
                                    onClick = { 
                                        viewModel.updateFilterRange(range)
                                        viewModel.toggleFilterDialog(false)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colors.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = range.label,
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.toggleFilterDialog(false) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colors.primary
                        )
                    ) {
                        Text("Закрыть")
                    }
                },
                backgroundColor = colors.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}