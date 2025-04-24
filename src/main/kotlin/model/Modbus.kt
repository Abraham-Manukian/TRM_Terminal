package model

import com.fazecast.jSerialComm.SerialPort

class Modbus {

    @OptIn(ExperimentalStdlibApi::class)
    fun sendRequest(portName: String, baudRate: Int, dataBits: Int, stopBits: Int, request: ByteArray): ByteArray {
        val port = SerialPort.getCommPort(portName)
        port.setComPortParameters(baudRate, dataBits, stopBits, 0)
        port.openPort()
        port.writeBytes(request, request.size)
        Thread.sleep(20)

        val buffer = ByteArray(256)
        val read = port.readBytes(buffer, buffer.size)
        port.closePort()

        println(request.toHexString())

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
        val crcBytes = crc.crcBytes
        req.addAll(crcBytes.toList())

        return req.toByteArray()
    }
}
