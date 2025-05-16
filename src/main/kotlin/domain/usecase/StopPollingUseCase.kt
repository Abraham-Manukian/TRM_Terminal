package domain.usecase

import org.example.domain.repository.PollingService

class StopPollingUseCase(
    private val pollingService: PollingService
) {
    operator fun invoke() {
        pollingService.stopAll()
    }
}
