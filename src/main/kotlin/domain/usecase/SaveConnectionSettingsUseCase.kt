package domain.usecase

import domain.service.NotificationService

/**
 * Use case для сохранения настроек подключения
 */
class SaveConnectionSettingsUseCase(private val notificationService: NotificationService) {
    /**
     * Сохранить настройки подключения
     * 
     * @param slaveAddress адрес slave-устройства
     */
    fun execute(slaveAddress: String) {
        // В реальном приложении здесь был бы код для сохранения настроек
        println("Сохранены настройки. Адрес: $slaveAddress")
        notificationService.showNotification("Настройки сохранены")
    }
} 