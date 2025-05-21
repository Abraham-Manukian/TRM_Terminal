package domain.usecase

import data.polling.PollingManager
import domain.model.PortConfig

class UpdatePortConfigUseCase(
    private val pollingManager: PollingManager
) {
    operator fun invoke(config: PortConfig) {
        pollingManager.updateConfig(config)
    }
}