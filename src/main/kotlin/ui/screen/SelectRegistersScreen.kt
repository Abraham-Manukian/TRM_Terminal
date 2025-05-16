package ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.launch
import ui.viewmodel.SelectRegistersViewModel
import domain.model.Register
import state.FilterRange

class SelectRegistersScreen(
    private val viewModel: SelectRegistersViewModel
) : Screen {

    @Composable
    override fun Content() {
        val nav           = LocalNavigator.current
        val scaffoldState = rememberScaffoldState()
        val scope         = rememberCoroutineScope()
        val state by viewModel.uiState.collectAsState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("Выбор регистров") },
                    navigationIcon = {
                        IconButton(onClick = { nav?.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            nav?.push(ConnectionScreen(viewModel.connectionViewModel))
                        }) {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        }
                        IconButton(onClick = { viewModel.toggleFilterDialog(true) }) {
                            Icon(Icons.Default.FilterList, contentDescription = null)
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    )
                )

                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(state.filteredRegisters) { reg ->
                            RegisterRow(
                                register   = reg,
                                isSelected = state.isSelected(reg),
                                rawValue   = state.currentValues[reg.address],
                                enabled    = state.isConnected,
                                onToggle   = { viewModel.toggleRegisterSelection(reg) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.saveSelectedRegisters()
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("Сохранено")
                            }
                            nav?.pop()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Сохранить выбор")
                    }
                }
            }

            if (state.showFilterDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.toggleFilterDialog(false) },
                    title = { Text("Выберите диапазон") },
                    text = {
                        Column {
                            FilterRange.values().forEach { range ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateFilterRange(range)
                                            viewModel.toggleFilterDialog(false)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = state.filterRange == range,
                                        onClick = {
                                            viewModel.updateFilterRange(range)
                                            viewModel.toggleFilterDialog(false)
                                        }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(range.label)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.toggleFilterDialog(false) }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RegisterRow(
    register: Register,
    isSelected: Boolean,
    rawValue: Double?,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    val display = rawValue?.let { String.format("%.2f", it) } ?: "—"
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(enabled, onClick = onToggle),
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(register.name, style = MaterialTheme.typography.subtitle1)
                Text("Adr: ${register.address}  Val: $display", style = MaterialTheme.typography.caption)
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                enabled = enabled
            )
        }
    }
}
