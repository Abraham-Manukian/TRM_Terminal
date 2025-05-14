package data.repository

import com.fazecast.jSerialComm.SerialPort
import data.datasource.ModbusDataSource
import domain.model.PortConfig
import domain.repository.ModbusRepository


class ModbusRepositoryImpl(
    private val dataSource: ModbusDataSource
) : ModbusRepository {

    override fun getAvailablePorts(showAll: Boolean): Map<String, String> =
        SerialPort.getCommPorts()
            .filter { showAll || it.descriptivePortName != null }
            .associate { it.systemPortName to (it.descriptivePortName ?: it.systemPortName) }

    override fun sendRequest(config: PortConfig, request: ByteArray): ByteArray =
        dataSource.sendRequest(
            config.portName, config.baudRate, config.dataBits, config.stopBits, request
        )

    override fun generateRequest(
        slave: Int,
        function: Int,
        address: Int,
        quantity: Int
    ): ByteArray {
        return dataSource.generateRequest(slave, function, address, quantity)    }
}

//class ModbusRepositoryImpl : ModbusRepository {
//
//    override fun getAvailablePorts(showAll: Boolean): Map<String, String> {
//        return SerialPort.getCommPorts()
//            .filter {
//                showAll || it.descriptivePortName.contains("CP21", ignoreCase = true)
//            }
//            .associateBy(
//                keySelector = { it.systemPortName },
//                valueTransform = { "${it.systemPortName} (${it.descriptivePortName})" }
//            )
//    }
//
//    override fun sendRequest(config: PortConfig, request: ByteArray): ByteArray {
//        val port = SerialPort.getCommPort(config.portName)
//        port.setComPortParameters(config.baudRate, config.dataBits, config.stopBits, 0)
//        port.openPort()
//
//        port.writeBytes(request, request.size)
//        Thread.sleep(20)
//
//        val buffer = ByteArray(256)
//        val read = port.readBytes(buffer, buffer.size)
//        port.closePort()
//
//        return buffer.take(read).toByteArray()
//    }
//
//    override fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray {
//        val req = mutableListOf<Byte>()
//        req.add(slave.toByte())
//        req.add(function.toByte())
//        req.add((address shr 8).toByte())
//        req.add((address and 0xFF).toByte())
//        req.add((quantity shr 8).toByte())
//        req.add((quantity and 0xFF).toByte())
//
//        val crc = CRC16Modbus()
//        crc.update(req.toByteArray())
//        val crcBytes = crc.crcBytes
//
//        req.addAll(crcBytes.toList())
//        return req.toByteArray()
//    }
//}
