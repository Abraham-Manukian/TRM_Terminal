//package ui.screen
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.Button
//import androidx.compose.material.Checkbox
//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
//import androidx.compose.material.Text
//import androidx.compose.material.TopAppBar
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.runtime.Composable
//import cafe.adriel.voyager.core.screen.Screen
//import cafe.adriel.voyager.navigator.LocalNavigator
//import viewmodel.MainViewModel
//
//class SelectRegistersScreen(
//    private val viewModel: MainViewModel
//) : Screen {
//
//    @Composable
//    override fun Content() {
//        val navigator = LocalNavigator.current
//
//        Column {
//            TopAppBar(
//                title = { Text("Выбор регистров") },
//                navigationIcon = {
//                    IconButton(onClick = { navigator?.pop() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
//                    }
//                }
//            )
//
//            // Список регистров с чекбоксами
//            LazyColumn {
//                items(viewModel.availableRegisters) { register ->
//                    Row {
//                        Checkbox(
//                            checked = register.isSelected,
//                            onCheckedChange = { viewModel.toggleRegisterSelection(register) }
//                        )
//                        Text("${register.address} - ${register.name}")
//                    }
//                }
//            }
//
//            Button(onClick = {
//                viewModel.saveSelectedRegisters()
//                navigator?.pop()
//            }) {
//                Text("Сохранить выбор")
//            }
//        }
//    }
//}