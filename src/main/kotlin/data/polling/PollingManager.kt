package data.polling

import org.example.data.DevicePoller.checkResponsibilityAndNotify
import org.example.data.DevicePoller.connection
import org.example.data.DevicePoller.devs
import domain.model.PortConfig
import data.device.Avem4Controller
import domain.model.RegisterType
import kotlinx.coroutines.*
import org.example.data.DevicePoller
import org.example.domain.repository.PollingService
import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.utils.SerialParameters
import ru.avem.library.polling.DeviceRegister
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

        connection = Connection(
            adapterName      = config.portName,
            serialParameters = SerialParameters(
                config.dataBits,
                config.parity,
                config.stopBits,
                config.baudRate
            ),
            attemptCount = config.attemptCount,
            timeoutRead  = config.timeoutRead,
            timeoutWrite = config.timeoutWrite
        ).apply { connect() }

        controller = Avem4Controller(
            name            = "АВЭМ4${config.slaveId}",
            protocolAdapter = DevicePoller.main,
            id              = config.slaveId
        )

        controller.addTo(devs)
        with(controller) {
            // пример использований из utility:
            // DevicePoller.startPoll(controller.name, model.FREQUENCY_TM) { println(it) }
        }
        job = CoroutineScope(ioDispatcher).launch {
            while (isActive) {
                with(controller) {
                    readAllRegisters()
                    println(controller.getRegisterById(controller.model.FREQUENCY).value)
                }
            }
        }
    }

    override fun stopAll() {
        job?.cancel()
        fieldJobs.values.forEach { it.cancel() }
        try {
            controller.protocolAdapter.connection.disconnect()
        } catch (_: Exception) {}
    }

    override fun startFieldPolling(
        registerAddress: Int,
        onValue: (Double) -> Unit
    ) {
        if (!::controller.isInitialized) {
            startPolling(currentConfig)
        }
        fieldJobs[registerAddress]?.cancel()
        val job = CoroutineScope(ioDispatcher).launch {
            while (isActive) {
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
        val reg = controller.model.registers
            .values
            .first { it.address.toInt() == registerAddress }
        controller.writeRegister(reg, value)
    }

    override fun getRegisters(): List<domain.model.Register> {
        return controller.model.registers.entries.map { (idKey, devReg) ->
            // Определяем, в какой domain.RegisterType переводить
            val regType = when (devReg.valueType) {
                DeviceRegister.RegisterValueType.SHORT       -> RegisterType.DISCRETE
                DeviceRegister.RegisterValueType.FLOAT,
                DeviceRegister.RegisterValueType.INT32       -> RegisterType.ANALOG
                else                                         -> RegisterType.CONFIG
            }
            // Собираем domain.model.Register, имя берём из ключа мапы
            domain.model.Register(
                address     = devReg.address.toInt(),
                name        = idKey,
                description = "",
                type        = regType
            // readOnly оставляем по умолчанию = true
            )
        }
    }
}

fun <C : S, S> C.addTo(list: MutableSet<S>): C {
    list.add(this)
    DevicePoller.deviceControllers = DevicePoller.devs.associateBy { it.name }
    return this
}
