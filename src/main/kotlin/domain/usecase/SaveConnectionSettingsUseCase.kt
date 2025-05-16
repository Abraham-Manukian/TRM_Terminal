package domain.usecase

import domain.model.PortConfig
import domain.service.NotificationService


class SaveConnectionSettingsUseCase(
    //private val repo: SettingsRepository
) {
    operator fun invoke(cfg: PortConfig) {
        //repo.savePortConfig(cfg)
        print("Типо сохранил")
    }
}