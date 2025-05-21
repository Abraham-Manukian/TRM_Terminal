package di

import data.NotificationServiceImpl
import data.device.Avem4Controller
import domain.repository.SerialPortRepository
import domain.service.NotificationService
import domain.usecase.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ui.viewmodel.ConnectionViewModel
import ui.viewmodel.MainMenuViewModel
import ui.viewmodel.RequestViewModel
import ui.viewmodel.SelectRegistersViewModel
import data.polling.PollingManager
import data.repository.Avem4RegisterRepository
import data.repository.SerialPortRepositoryImpl
import domain.model.Register
import domain.repository.RegisterRepository
import org.example.domain.repository.PollingService
import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter

val appModule = module {

    // Адаптер для Modbus RTU
    single { Connection() }
    single { ModbusRTUAdapter(get()) }

    // Polling-сервис
    single<PollingService> { PollingManager() }
    single { PollingManager() }

    // Репозитории
    single<RegisterRepository> { Avem4RegisterRepository(get()) }
    single<SerialPortRepository> { SerialPortRepositoryImpl() }

    // Контроллер
    factory {
        Avem4Controller(
            name = "АВЭМ4",
            protocolAdapter = get(),
            id = 1
        )
    }

    // Polling-сервис
    single<PollingService> { PollingManager() }
    single { PollingManager() }

    // Репозитории
    single<RegisterRepository> { Avem4RegisterRepository(get()) }
    single<SerialPortRepository> { SerialPortRepositoryImpl() }

    // Use-case-зависимые данные
    single<Register>(named("frequency_tm")) {
        GetRegisterByAddressUseCase(get())(0x1016)
    }

    // Services
    single<NotificationService> { NotificationServiceImpl() }

    // Device controller
    factory {
        Avem4Controller(
            name = "АВЭМ4",
            protocolAdapter = get<ModbusRTUAdapter>(),
            id = 1
        )
    }

    // ViewModels
    factory { ConnectionViewModel(get(), get(), get(), get()) }
    single { MainMenuViewModel() }
    single {
        RequestViewModel(
            updatePortConfig = get(),
            startPolling = get(),
            stopPolling = get(),
            readOnce = get(),
            writeRegister = get(),
            saveConnectionSettings = get(),
            frequencyRegister = get(named("frequency_tm")),
            connectionViewModel = get()
        )
    }
    single { SelectRegistersViewModel(get(), get(), get()) }
}
