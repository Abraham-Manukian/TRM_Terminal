package domain.repository

import domain.model.PortConfig


interface ModbusRepository {
    fun getAvailablePorts(showAll: Boolean): Map<String, String>
    fun sendRequest(config: PortConfig, request: ByteArray): ByteArray
    fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray
}


