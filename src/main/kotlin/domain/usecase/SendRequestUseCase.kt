package domain.usecase

import domain.repository.ModbusRepository

class SendRequestUseCase(
    private val repository: ModbusRepository
) {
    suspend fun sendRequest(
        portName: String,
        baudRate: Int,
        dataBits: Int,
        stopBits: Int,
        request: ByteArray
    ): ByteArray {
        return repository.sendRequest(
            portName = portName,
            baudRate = baudRate,
            dataBits = dataBits,
            stopBits = stopBits,
            request = request
        )
    }
}
