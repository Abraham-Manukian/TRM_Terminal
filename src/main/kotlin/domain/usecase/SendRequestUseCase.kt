package domain.usecase

import domain.repository.ModbusRepository

/**
 * Use case для отправки запроса к устройству
 */
class SendRequestUseCase(private val repository: ModbusRepository) {
    /**
     * Отправить запрос к устройству
     * 
     * @param portName имя порта
     * @param baudRate скорость порта
     * @param dataBits биты данных
     * @param stopBits стоп-биты
     * @param request данные запроса
     * @return ответ от устройства
     */
    fun sendRequest(
        portName: String,
        baudRate: Int,
        dataBits: Int,
        stopBits: Int,
        request: ByteArray
    ): ByteArray {
        // В реальном приложении здесь был бы код для отправки запроса через repository
        // Для демонстрации возвращаем заглушку
        return byteArrayOf(
            0x01.toByte(), 
            0x03.toByte(), 
            0x04.toByte(), 
            0x00.toByte(), 
            0xFF.toByte(), 
            0x00.toByte(), 
            0x01.toByte(), 
            0xFA.toByte(), 
            0xAC.toByte()
        )
    }
}
