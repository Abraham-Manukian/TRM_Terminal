package domain.usecase

import domain.model.PortConfig
import domain.repository.ModbusRepository


class SendRawRequestUseCase(private val modbusRepository: ModbusRepository) {

    operator fun invoke(rawHex: String, config: PortConfig): Result<Pair<ByteArray, ByteArray>> {
        return runCatching {
            val request = rawHex
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.toInt(16).toByte() }
                .toByteArray()

            val response = modbusRepository.sendRequest(config, request)
            request to response
        }
    }
}
