package data

import com.fazecast.jSerialComm.SerialPort
import core.CRC16Modbus
import java.io.IOException

class Modbus {

    @OptIn(ExperimentalStdlibApi::class)
    fun sendRequest(portName: String, baudRate: Int, dataBits: Int, stopBits: Int, request: ByteArray): ByteArray {
        val port = SerialPort.getCommPort(portName)
        try {
            port.setComPortParameters(baudRate, dataBits, stopBits, 0)
            if (!port.openPort()) {
                throw IOException("Не удалось открыть порт $portName")
            }
            
            val bytesWritten = port.writeBytes(request, request.size)
            if (bytesWritten != request.size) {
                throw IOException("Ошибка при отправке данных: отправлено $bytesWritten из ${request.size} байт")
            }
            
            Thread.sleep(20)

            val buffer = ByteArray(256)
            val read = port.readBytes(buffer, buffer.size)
            
            if (read <= 0) {
                throw IOException("Не получен ответ от устройства")
            }
            
            println("Отправлено: ${request.toHexString()}")
            println("Получено ${read} байт")
            
            return buffer.take(read).toByteArray()
        } catch (e: Exception) {
            throw IOException("Ошибка работы с портом: ${e.message}", e)
        } finally {
            if (port.isOpen) {
                port.closePort()
            }
        }
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