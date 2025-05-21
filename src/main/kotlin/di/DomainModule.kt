package di

import domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    single { StartPollingUseCase(get()) }
    single { StopPollingUseCase(get()) }
    single { StartSingleRegisterPollingUseCase(get()) }
    single { StopSingleRegisterPollingUseCase(get()) }
    single { GetRegisterByAddressUseCase(get()) }
    single { SaveConnectionSettingsUseCase() }
    single { ReadRegisterOnceUseCase(get()) }
    single { WriteRegisterUseCase(get()) }
    single { LoadPortsUseCase(get()) }
    single { UpdatePortConfigUseCase(get()) }
}
