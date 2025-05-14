package domain.usecase

import domain.polling.PollingService

class StopPollingUseCase(
    private val pollingService: PollingService
) {
    operator fun invoke() {
        pollingService.stopAll()
    }
}
