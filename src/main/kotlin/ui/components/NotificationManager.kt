package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object NotificationManager {
    var message by mutableStateOf<String?>(null)

    fun show(message: String, durationMillis: Long = 3000) {
        this.message = message
        CoroutineScope(Dispatchers.Default).launch {
            delay(durationMillis)
            this@NotificationManager.message = null
        }
    }
}

@Composable
fun NotificationPopup() {
    val msg = NotificationManager.message
    if (msg != null) {
        Popup(alignment = Alignment.TopCenter) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(text = msg, color = MaterialTheme.colors.onPrimary)
            }
        }
    }
}