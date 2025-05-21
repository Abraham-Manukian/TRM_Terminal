package domain.usecase

import domain.model.Register
import data.polling.PollingManager

class ReadRegisterOnceUseCase(
    private val pollingManager: PollingManager
) {
    suspend operator fun invoke(register: Register): Result<Float> {
        return try {
            val result = pollingManager.readRegisterOnce(register.address)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

