package di

import data.datasource.ModbusDataSourceImpl
import data.repository.ModbusRepositoryImpl
import data.repository.SerialPortRepositoryImpl
import data.NotificationServiceImpl
import data.adapter.ModbusAdapter
import domain.repository.ModbusRepository
import domain.repository.SerialPortRepository
import domain.service.NotificationService
import domain.usecase.*
import org.koin.dsl.module
import ui.viewmodel.ConnectionViewModel
import ui.viewmodel.MainMenuViewModel
import ui.viewmodel.RequestViewModel
import ui.viewmodel.SelectRegistersViewModel
import data.datasource.ModbusDataSource

import data.polling.PollingManager
import domain.model.PortConfig
import domain.polling.PollingService

val appModule = module {
    // Data sources
    single<ModbusDataSource> { ModbusDataSourceImpl() }
    
    // Repositories
    single<ModbusRepository> { ModbusRepositoryImpl(get()) }
    single<SerialPortRepository> { SerialPortRepositoryImpl() }

    // Connection config
    factory { (cfg: PortConfig) -> ModbusAdapter(cfg) }


    // Polling-сервис
    single<PollingService> { PollingManager() }
    single { StartPollingUseCase(get()) }
    single { StopPollingUseCase(get()) }

    
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
    single { ConnectionViewModel(get(), get()) }
    single { RequestViewModel(get(), get(), get(), get(), get()) }
    single { SelectRegistersViewModel(pollingService = get ()) }
}