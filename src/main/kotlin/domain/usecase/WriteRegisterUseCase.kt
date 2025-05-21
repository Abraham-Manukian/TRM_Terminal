package domain.usecase

import domain.model.Register
import data.polling.PollingManager

class WriteRegisterUseCase(
    private val pollingManager: PollingManager
) {
    suspend operator fun invoke(register: Register, value: Float): Result<Unit> {
        return try {
            pollingManager.writeRegister(register.address, value)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
