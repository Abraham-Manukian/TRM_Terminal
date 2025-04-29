package data.datasource

/**
 * Интерфейс источника данных для работы с Modbus-устройствами
 */
interface ModbusDataSource {
    /**
     * Отправить байты запроса и получить ответ
     * 
     * @param portName имя порта
     * @param baudRate скорость порта
     * @param dataBits биты данных
     * @param stopBits стоп-биты
     * @param request байты запроса
     * @return байты ответа
     */
    fun sendRequest(
        portName: String, 
        baudRate: Int, 
        dataBits: Int, 
        stopBits: Int, 
        request: ByteArray
    ): ByteArray

    /**
     * Сформировать байты Modbus-запроса
     * 
     * @param slave адрес устройства
     * @param function код функции
     * @param address адрес регистра
     * @param quantity количество регистров
     * @return байты готового запроса с CRC
     */
    fun generateRequest(slave: Int, function: Int, address: Int, quantity: Int): ByteArray
} 