package domain.repository

import domain.model.PortInfo

/**
 * Репозиторий для работы с последовательными портами
 */
interface SerialPortRepository {
    /**
     * Получить список всех доступных COM-портов
     * @return список информации о доступных портах
     */
    fun getAvailablePorts(): List<PortInfo>
} 