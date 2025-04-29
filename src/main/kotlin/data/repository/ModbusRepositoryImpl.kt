package data.repository

import data.datasource.ModbusDataSource
import domain.repository.ModbusRepository

/**
 * Реализация репозитория для работы с Modbus-устройствами
 */
class ModbusRepositoryImpl(private val modbusDataSource: ModbusDataSource) : ModbusRepository {

    override suspend fun sendRequest(
        portName: String,
        baudRate: Int,
        dataBits: Int,
        stopBits: Int,
        request: ByteArray
    ): ByteArray {
        return modbusDataSource.sendRequest(portName, baudRate, dataBits, stopBits, request)
    }

    override fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray {
        return modbusDataSource.generateRequest(slave, function, address, quantity)
    }
} 