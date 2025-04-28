package data

import data.Modbus
import domain.repository.ModbusRepository

class ModbusRepositoryImpl : ModbusRepository {

    private val modbus = Modbus()

    override suspend fun sendRequest(
        portName: String,
        baudRate: Int,
        dataBits: Int,
        stopBits: Int,
        request: ByteArray
    ): ByteArray {
        return modbus.sendRequest(portName, baudRate, dataBits, stopBits, request)
    }

    override fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray {
        return modbus.generateRequest(slave, function, address, quantity)
    }
}