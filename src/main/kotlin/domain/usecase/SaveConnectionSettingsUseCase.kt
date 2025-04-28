package domain.usecase

import domain.service.NotificationService

class SaveConnectionSettingsUseCase(
    private val notificationService: NotificationService
) {
    fun execute(slaveAddress: String) {
        val address = slaveAddress.toIntOrNull()
        if (address in 1..247) {
            notificationService.showNotification("Настройки сохранены")
        } else {
            notificationService.showNotification("Ошибка: Неверный адрес")
        }
    }
} 