package data.repository

import com.fazecast.jSerialComm.SerialPort
import domain.model.PortInfo
import domain.repository.SerialPortRepository

/**
 * Реализация репозитория для работы с последовательными портами
 */
class SerialPortRepositoryImpl : SerialPortRepository {
    
    override fun getAvailablePorts(): List<PortInfo> {
        try {
            // Получаем все доступные порты из системы
            val availablePorts = SerialPort.getCommPorts()
            
            // Преобразуем в нашу модель
            return availablePorts.map { port ->
                PortInfo(
                    systemName = port.systemPortName,
                    displayName = port.descriptivePortName
                )
            }
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустой список
            println("Ошибка при загрузке портов: ${e.message}")
            return emptyList()
        }
    }
} 