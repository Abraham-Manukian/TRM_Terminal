package data.adapter

import domain.model.PortConfig
import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.BitVector
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.SerialParameters


class ModbusAdapter(private val cfg: PortConfig) {
    private val connection = Connection(
        adapterName      = cfg.portName,
        serialParameters = SerialParameters(
            cfg.dataBits,
            cfg.parity,
            cfg.stopBits,
            cfg.baudRate
        ),
        attemptCount = cfg.attemptCount,
        timeoutRead  = cfg.timeoutRead,
        timeoutWrite = cfg.timeoutWrite
    ).apply { connect() }

    private val adapter = ModbusRTUAdapter(connection)

    // Чтение holding-регистров
    fun readHoldingRegisters(
        deviceId: Byte,
        registerId: Short,
        count: Int,
        customBaudrate: Int? = null
    ): List<ModbusRegister> =
        adapter.readHoldingRegisters(deviceId, registerId, count, customBaudrate)

    // Чтение input-регистров
    fun readInputRegisters(
        deviceId: Byte,
        registerId: Short,
        count: Int,
        customBaudrate: Int? = null
    ): List<ModbusRegister> =
        adapter.readInputRegisters(deviceId, registerId, count, customBaudrate)

    // Чтение coil-статуса
    fun readCoilStatus(
        deviceId: Byte,
        registerId: Short,
        count: Int,
        customBaudrate: Int? = null
    ): BitVector =
        adapter.readCoilStatus(deviceId, registerId, count, customBaudrate)

    // Чтение discrete-inputs
    fun readDiscreteInputs(
        deviceId: Byte,
        registerId: Short,
        count: Int,
        customBaudrate: Int? = null
    ): BitVector =
        adapter.readDiscreteInputs(deviceId, registerId, count, customBaudrate)

    // Запись одного holding-регистра
    fun presetSingleRegister(
        deviceId: Byte,
        registerId: Short,
        register: ModbusRegister,
        customBaudrate: Int? = null
    ) =
        adapter.presetSingleRegister(deviceId, registerId, register, customBaudrate)

    // Запись нескольких holding-регистров
    fun presetMultipleRegisters(
        deviceId: Byte,
        registerId: Short,
        registers: List<ModbusRegister>,
        customBaudrate: Int? = null
    ) =
        adapter.presetMultipleRegisters(deviceId, registerId, registers, customBaudrate)

    // Force coils
    fun forceSingleCoil(
        deviceId: Byte,
        registerId: Short,
        value: Boolean,
        customBaudrate: Int? = null
    ) =
        adapter.forceSingleCoil(deviceId, registerId, value, customBaudrate)

    fun forceMultipleCoils(
        deviceId: Byte,
        registerId: Short,
        coils: BitVector,
        customBaudrate: Int? = null
    ) =
        adapter.forceMultipleCoils(deviceId, registerId, coils, customBaudrate)

    // Отключение порта
    fun disconnect() = connection.disconnect()
}
