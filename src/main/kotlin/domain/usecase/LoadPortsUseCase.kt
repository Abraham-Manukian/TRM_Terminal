package domain.usecase

import com.fazecast.jSerialComm.SerialPort
import domain.model.PortInfo

class LoadPortsUseCase {
    fun execute(showAllPorts: Boolean): List<PortInfo> {
        return SerialPort.getCommPorts()
            .filter {
                showAllPorts || it.descriptivePortName.contains("CP21", ignoreCase = true)
            }
            .map { port ->
                PortInfo(
                    systemName = port.systemPortName,
                    displayName = "${port.systemPortName} (${port.descriptivePortName})"
                )
            }
    }
} 