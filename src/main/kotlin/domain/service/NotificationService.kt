package domain.service

interface NotificationService {
    fun showNotification(message: String, durationMillis: Long = 3000)
} 