package domain.usecase

import data.polling.PollingManager
import domain.model.Register

class StartSingleRegisterPollingUseCase(
    private val pollingManager: PollingManager
) {
    operator fun invoke(register: Register, onUpdate: (Double) -> Unit) {
        pollingManager.startFieldPolling(register.address, onUpdate)
    }
}