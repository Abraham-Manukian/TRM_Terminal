package domain.repository

interface ModbusRepository {
    suspend fun sendRequest(portName: String,
                            baudRate: Int,
                            dataBits: Int,
                            stopBits: Int,
                            request: ByteArray
    ) : ByteArray
    
    fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray
}