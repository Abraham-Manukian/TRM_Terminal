package domain.usecase

import domain.model.PortInfo
import domain.repository.SerialPortRepository


class LoadPortsUseCase(private val serialPortRepository: SerialPortRepository) {
    fun execute(showAll: Boolean = false): List<PortInfo> {
        // Получаем порты из репозитория
        val portInfoList = serialPortRepository.getAvailablePorts()
        
        // Фильтруем, если нужно показать только порты с CP21 в имени
        return if (showAll) {
            portInfoList
        } else {
            portInfoList.filter { 
                it.systemName.contains("CP21", ignoreCase = true) || 
                it.displayName.contains("CP21", ignoreCase = true) 
            }
        }
    }
} 