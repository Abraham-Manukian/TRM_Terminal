package org.example.data.source

import com.fazecast.jSerialComm.SerialPort
import core.CRC16Modbus


class ModbusPort {

    fun getAvailablePorts(showAll: Boolean): Map<String, String> {
        return SerialPort.getCommPorts()
            .filter { showAll || it.descriptivePortName.contains("CP21", ignoreCase = true) }
            .associateBy({ it.systemPortName }, { "${it.systemPortName} (${it.descriptivePortName})" })
    }

    fun sendRequest(portName: String, baudRate: Int, dataBits: Int, stopBits: Int, request: ByteArray): ByteArray {
        val port = SerialPort.getCommPort(portName)
        port.setComPortParameters(baudRate, dataBits, stopBits, 0)
        port.openPort()
        port.writeBytes(request, request.size)
        Thread.sleep(20)
        val buffer = ByteArray(256)
        val read = port.readBytes(buffer, buffer.size)
        port.closePort()
        return buffer.take(read).toByteArray()
    }

    fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray {
        val req = mutableListOf<Byte>()
        req.add(slave.toByte())
        req.add(function.toByte())
        req.add((address shr 8).toByte())
        req.add((address and 0xFF).toByte())
        req.add((quantity shr 8).toByte())
        req.add((quantity and 0xFF).toByte())

        val crc = CRC16Modbus()
        crc.update(req.toByteArray())
        req.addAll(crc.crcBytes.toList())

        return req.toByteArray()
    }
}