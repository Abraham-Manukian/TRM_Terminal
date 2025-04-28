package di

import data.Modbus
import data.ModbusRepositoryImpl
import data.NotificationServiceImpl
import domain.repository.ModbusRepository
import domain.service.NotificationService
import domain.usecase.*
import org.koin.dsl.module
import ui.viewmodel.ConnectionViewModel
import ui.viewmodel.MainMenuViewModel
import ui.viewmodel.RequestViewModel

val appModule = module {
    // Repositories
    single<ModbusRepository> { ModbusRepositoryImpl() }
    
    // Services
    single<NotificationService> { NotificationServiceImpl() }
    
    // Use cases
    single { SendRequestUseCase(get()) }
    single { GenerateRequestUseCase(get()) }
    single { SaveConnectionSettingsUseCase(get()) }
    single { FormatResponseUseCase() }
    single { LoadPortsUseCase() }
    
    // Data sources
    single { Modbus() }
    
    // ViewModels
    factory { MainMenuViewModel() }
    factory { ConnectionViewModel(get(), get(), get()) }
    factory { RequestViewModel(get(), get(), get(), get()) }
}