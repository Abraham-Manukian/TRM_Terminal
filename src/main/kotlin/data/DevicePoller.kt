package org.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.PortDiscover
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.utils.SerialParameters
import ru.avem.library.polling.IDeviceController
import ru.avem.library.polling.SimplePollingModel

object DevicePoller : SimplePollingModel() {
    var connection = Connection(
        adapterName = "CP2103 USB to RS-485",//CP2103 USB to FI "CP2103 USB to RS-485"/*"CP2103 USB to HPMAIN",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        attemptCount = 5,
        timeoutRead = 100,
        timeoutWrite = 100
    )
    var connections = mutableListOf<Connection>(connection)

    fun IDeviceController.checkResponsibilityAndNotify(): Boolean {
        with(this) {
            checkResponsibility()
            if (!isResponding) {
                connection.disconnect()
                Thread.sleep(500)
                connection.connect()
                Thread.sleep(500)
                if (!checkCP2103()) {
                    println("Не подключен преобразователь интерфейса RS-485")
                    return false
                } else {
                    return true
                }
            }
            return true
        }
    }

    private fun checkCP2103() =
        PortDiscover.ports.any { it.portDescription == connection.adapterName }

    init {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                connections.forEach { it.connect() }
            }
            start()
        } catch (e: Exception) {
            stop()
            connections.forEach { it.disconnect() }
            throw e
        }
    }

    val main = ModbusRTUAdapter(connection)

    var devs = mutableSetOf<IDeviceController>()

    override var deviceControllers: Map<String, IDeviceController> = devs.associateBy { it.name }
}