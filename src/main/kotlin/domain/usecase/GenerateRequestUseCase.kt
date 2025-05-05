package domain.usecase

import domain.repository.ModbusRepository
import state.ByteOrder

/**
 * Use case для генерации запроса 
 */
class GenerateRequestUseCase(private val repository: ModbusRepository) {
    
    /**
     * Сгенерировать запрос на основе параметров
     * 
     * @param slave адрес slave-устройства
     * @param functionCode код функции Modbus
     * @param address адрес регистра
     * @param quantity количество регистров
     * @return сгенерированный запрос
     */

    fun execute(slave: Int, functionCode: Int, address: Int, quantity: Int): ByteArray {
        return repository.generateRequest(slave, functionCode, address, quantity)
    }
} 