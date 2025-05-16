package di


import data.NotificationServiceImpl
import domain.repository.SerialPortRepository
import domain.service.NotificationService
import domain.usecase.*
import org.koin.dsl.module
import ui.viewmodel.ConnectionViewModel
import ui.viewmodel.MainMenuViewModel
import ui.viewmodel.RequestViewModel
import ui.viewmodel.SelectRegistersViewModel

import data.polling.PollingManager
import data.repository.SerialPortRepositoryImpl
import org.example.domain.repository.PollingService

val appModule = module {

    // Polling-сервис
    single<PollingService> { PollingManager() }
    single { StartPollingUseCase(get()) }
    single { StopPollingUseCase(get()) }

    single<SerialPortRepository> {
        SerialPortRepositoryImpl()
    }
    
    // Services
    single<NotificationService> { NotificationServiceImpl() }
    
    // Use cases
    single { SendRawRequestUseCase(get()) }
    single { GenerateRequestUseCase(get()) }
    single { SaveConnectionSettingsUseCase() }
    single { FormatResponseUseCase() }
    single { LoadPortsUseCase(get()) }
    
    // ViewModels
    single { MainMenuViewModel() }
    single { ConnectionViewModel(get(), get(), get(), get()) }
    single { RequestViewModel(get(), get(), get(), get(), get()) }
    single { SelectRegistersViewModel(get(), get (), get()) }
}