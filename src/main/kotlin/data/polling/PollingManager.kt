package data.polling

import data.adapter.ModbusAdapter
import domain.model.PortConfig
import domain.polling.PollingService
import data.device.Avem4Controller
import kotlinx.coroutines.*
import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.utils.SerialParameters
import java.util.concurrent.ConcurrentHashMap


/**
 * Менеджер фонового опроса и одиночных полей.
 */
class PollingManager(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PollingService {
    private var job: Job? = null
    private lateinit var controller: Avem4Controller
    private lateinit var currentConfig: PortConfig
    private val fieldJobs = ConcurrentHashMap<Int, Job>()

    override fun startPolling(config: PortConfig) {
        stopAll()
        currentConfig = config

        // 1) Открываем порт
        val connection = Connection(
            adapterName = config.portName,
            serialParameters = SerialParameters(
                config.dataBits,
                config.parity,
                config.stopBits,
                config.baudRate
            ),
            attemptCount = config.attemptCount,
            timeoutRead = config.timeoutRead,
            timeoutWrite = config.timeoutWrite
        ).apply { connect() }

        // 2) Создаём RTU-адаптер из kserialpooler
        val rtuAdapter = ModbusRTUAdapter(connection)

        // 3) Передаём его в контроллер — вот здесь была ваша ошибка
        controller = Avem4Controller(
            name            = "АВЭМ4${config.slaveId}",
            protocolAdapter = rtuAdapter,        // теперь правильный тип!
            id              = config.slaveId
        )

        // 4) Стартуем фоновый цикл опроса всех регистров
        job = CoroutineScope(ioDispatcher).launch {
            while (isActive) {
                controller.readAllRegisters()
                delay(config.pollIntervalMillis)
            }
        }
    }

    override fun stopAll() {
        job?.cancel()
        fieldJobs.values.forEach { it.cancel() }
        try {
            // Корректно разрываем соединение
            controller.protocolAdapter.connection.disconnect()
        } catch (_: Exception) {}
    }

    override fun startFieldPolling(
        registerAddress: Int,
        onValue: (Double) -> Unit
    ) {
        // Отменяем прошлый таск, если есть
        fieldJobs[registerAddress]?.cancel()

        // Новый цикл
        val job = CoroutineScope(ioDispatcher).launch {
            while (isActive) {
                // Ищем нужный регистр в модели по адресу
                val reg = controller.model.registers
                    .values
                    .first { it.address.toInt() == registerAddress }
                controller.readRegister(reg)
                onValue(reg.value.toDouble())
                delay(currentConfig.pollIntervalMillis)
            }
        }
        fieldJobs[registerAddress] = job
    }

    override fun writeRegister(registerAddress: Int, value: Number) {
        // Находим регистр и вызываем контроллер
        val reg = controller.model.registers
            .values
            .first { it.address.toInt() == registerAddress }
        controller.writeRegister(reg, value)
    }
}
