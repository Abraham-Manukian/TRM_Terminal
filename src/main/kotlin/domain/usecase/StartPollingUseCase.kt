package domain.usecase

import domain.model.PortConfig
import org.example.domain.repository.PollingService

class StartPollingUseCase(
    private val pollingService: PollingService
) {
    operator fun invoke(config: PortConfig) {
        pollingService.startPolling(config)
    }
}