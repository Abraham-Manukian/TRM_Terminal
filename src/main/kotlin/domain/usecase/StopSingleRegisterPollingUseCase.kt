package domain.usecase

import data.polling.PollingManager
import domain.model.Register

class StopSingleRegisterPollingUseCase(
    private val pollingManager: PollingManager
) {
    operator fun invoke(register: Register) {
        pollingManager.stopFieldPolling(register.address)
    }
}
