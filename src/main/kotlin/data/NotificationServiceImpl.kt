package data

import domain.service.NotificationService
import ui.components.NotificationManager

class NotificationServiceImpl : NotificationService {
    override fun showNotification(message: String, durationMillis: Long) {
        NotificationManager.show(message, durationMillis)
    }
} 