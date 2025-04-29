package ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.compose.koinInject
import ui.components.AnimatedButton
import ui.viewmodel.ConnectionViewModel
import ui.viewmodel.MainMenuViewModel
import ui.viewmodel.RequestViewModel
import ui.viewmodel.SelectRegistersViewModel

class MainMenuScreen(
    private val viewModel: MainMenuViewModel
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val connectionViewModel = koinInject<ConnectionViewModel>()
        val requestViewModel = koinInject<RequestViewModel>()
        val selectRegistersViewModel = koinInject<SelectRegistersViewModel>()
        var visible by remember { mutableStateOf(false) }
        
        // Запуск анимации появления при первом отображении
        LaunchedEffect(Unit) {
            visible = true
        }

        // Анимированный цвет фона
        val backgroundColor by animateColorAsState(
            targetValue = MaterialTheme.colors.background,
            animationSpec = tween(500)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            // Анимированное появление контента
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1000)) + 
                        slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight }, 
                            animationSpec = tween(1000, easing = EaseOutCubic)
                        ),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { fullHeight -> fullHeight })
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .shadow(
                            elevation = animateDpAsState(
                                targetValue = if (viewModel.themeState.isDarkTheme) 12.dp else 8.dp,
                                animationSpec = tween(500)
                            ).value,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp)),
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .width(320.dp)
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Анимированный заголовок
                        Text(
                            "TRM1 Terminal",
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                        )

                        // Кнопка настроек с анимациями
                        AnimatedButton(
                            onClick = { navigator?.push(ConnectionScreen(connectionViewModel)) },
                            icon = Icons.Default.Settings,
                            text = "Настройки подключения",
                            primaryColor = MaterialTheme.colors.primary,
                            secondaryColor = MaterialTheme.colors.primaryVariant
                        )

                        // Кнопка опроса данных с анимациями
                        AnimatedButton(
                            onClick = { navigator?.push(RequestScreen(requestViewModel)) },
                            icon = Icons.Default.DataUsage,
                            text = "Опрос данных",
                            primaryColor = MaterialTheme.colors.primary, 
                            secondaryColor = MaterialTheme.colors.primaryVariant
                        )
                        
                        // Кнопка выбора регистров с анимациями
                        AnimatedButton(
                            onClick = { navigator?.push(SelectRegistersScreen(selectRegistersViewModel)) },
                            icon = Icons.Default.List,
                            text = "Выбор регистров",
                            primaryColor = MaterialTheme.colors.primary, 
                            secondaryColor = MaterialTheme.colors.primaryVariant
                        )
                    }
                }
            }

            // Анимированная кнопка переключения темы
            val rotation by animateFloatAsState(
                targetValue = if (viewModel.themeState.isDarkTheme) 180f else 0f,
                animationSpec = tween(500, easing = EaseInOutCubic)
            )

            IconButton(
                onClick = { viewModel.toggleTheme() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.themeState.isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Переключение темы",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
            }
        }
    }
}