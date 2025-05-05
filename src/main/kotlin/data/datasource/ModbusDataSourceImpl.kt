package data.datasource

import com.fazecast.jSerialComm.SerialPort
import core.CRC16Modbus
import java.io.IOException
import java.lang.Thread.sleep

lateinit var port: SerialPort

class ModbusDataSourceImpl : ModbusDataSource {

    init {
        port = SerialPort.getCommPorts().find { it.portDescription.contains("CP2103 USB to RS-485") }!!
        port.setComPortParameters(38400, 8, 1, 0)
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 100)
        port.openPort()
    }

    override fun sendRequest(
        portName: String, baudRate: Int, dataBits: Int, stopBits: Int, request: ByteArray
    ): ByteArray {
        try {
            val buffer = ByteArray(256)
            port.flushIOBuffers()

            val bytesWritten = port.writeBytes(request, request.size)
            if (bytesWritten != request.size) {
                throw IOException("Ошибка при отправке данных: отправлено $bytesWritten из ${request.size} байт")
            }
            var read: Int
            do {
                read = port.readBytes(buffer, buffer.size)
                sleep(1)
            } while (read == 0)

            return buffer.take(read).toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Ошибка работы с портом: ${e.message}", e)
        } finally {
//            if (port.isOpen) {
//                println("Закрываем порт")
//                port.closePort()
//            }
        }
    }

    override fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray {
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