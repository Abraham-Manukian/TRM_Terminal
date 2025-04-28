package domain.usecase

import domain.repository.ModbusRepository

class GenerateRequestUseCase(
    private val repository: ModbusRepository
) {
    fun execute(slave: Int, functionCode: Int, address: Int, quantity: Int): ByteArray {
        return repository.generateRequest(slave, functionCode, address, quantity)
    }
} 