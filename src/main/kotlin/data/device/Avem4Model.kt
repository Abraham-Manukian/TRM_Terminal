package data.device

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel


class Avem4Model : IDeviceModel {
    val RMS_VOLTAGE = "RMS_VOLTAGE"
    val AMP_VOLTAGE = "AMP_VOLTAGE"
    val AVR_VOLTAGE = "AVR_VOLTAGE"
    val FREQUENCY = "FREQUENCY"
    val RMS_VOLTAGE_TM = "RMS_VOLTAGE_TM"
    val AMP_VOLTAGE_TM = "AMP_VOLTAGE_TM"
    val AVR_VOLTAGE_TM = "AVR_VOLTAGE_TM"
    val FREQUENCY_TM = "FREQUENCY_TM"
    val KTR_RUNTIME = "KTR_RUNTIME"
    val SERIAL_NUMBER = "SERIAL_NUMBER"
    val SOFTWARE_DATE = "SOFTWARE_DATE"
    val KTR_FLASH = "KTR_FLASH"
    val SHOW_VALUE = "SHOW_VALUE"
    val CHART_POINTS_BEGIN = "CHART_POINTS_BEGIN"
    val CHART_POINTS_END = "CHART_POINTS_END"
    val CHART_TIME = "CHART_TIME"
    val START_CHART = "START_CHART"
    val TRIGGER_MODE = "TRIGGER_MODE"
    val TRIGGER_VALUE = "TRIGGER_VALUE"
    val AMPLITUDE_COEFFICIENT = "AMPLITUDE_COEFFICIENT"
    val FORM_COEFFICIENT = "FORM_COEFFICIENT"
    val AMPLITUDE_COEFFICIENT_TM = "AMPLITUDE_COEFFICIENT_TM"
    val FORM_COEFFICIENT_TM = "FORM_COEFFICIENT_TM"
    val TIME_AVERAGING = "TIME_AVERAGING"

    override val registers: Map<String, DeviceRegister> = mapOf(
        RMS_VOLTAGE to DeviceRegister(0x1004, DeviceRegister.RegisterValueType.FLOAT, "В"),
        AMP_VOLTAGE to DeviceRegister(0x1000, DeviceRegister.RegisterValueType.FLOAT, "В"),
        AVR_VOLTAGE to DeviceRegister(0x1002, DeviceRegister.RegisterValueType.FLOAT, "В"),
        FREQUENCY to DeviceRegister(0x1006, DeviceRegister.RegisterValueType.FLOAT, "Гц"),
        RMS_VOLTAGE_TM to DeviceRegister(0x1014, DeviceRegister.RegisterValueType.FLOAT, "В"),
        AMP_VOLTAGE_TM to DeviceRegister(0x1010, DeviceRegister.RegisterValueType.FLOAT, "В"),
        AVR_VOLTAGE_TM to DeviceRegister(0x1012, DeviceRegister.RegisterValueType.FLOAT, "В"),
        FREQUENCY_TM to DeviceRegister(0x1016, DeviceRegister.RegisterValueType.FLOAT, "Гц"),
        KTR_RUNTIME to DeviceRegister(0x10BC, DeviceRegister.RegisterValueType.FLOAT),
        SERIAL_NUMBER to DeviceRegister(0x1108, DeviceRegister.RegisterValueType.INT32),
        SOFTWARE_DATE to DeviceRegister(0x1022, DeviceRegister.RegisterValueType.INT32),
        KTR_FLASH to DeviceRegister(0x10CE, DeviceRegister.RegisterValueType.FLOAT),
        SHOW_VALUE to DeviceRegister(0x10D8, DeviceRegister.RegisterValueType.INT32),
        CHART_POINTS_BEGIN to DeviceRegister(0xE00A.toShort(), DeviceRegister.RegisterValueType.FLOAT),
        CHART_POINTS_END to DeviceRegister(0xFF49.toShort(), DeviceRegister.RegisterValueType.FLOAT),
        CHART_TIME to DeviceRegister(0xE002.toShort(), DeviceRegister.RegisterValueType.INT32),
        START_CHART to DeviceRegister(0xE005.toShort(), DeviceRegister.RegisterValueType.SHORT),
        TRIGGER_MODE to DeviceRegister(0xE004.toShort(), DeviceRegister.RegisterValueType.SHORT),
        TRIGGER_VALUE to DeviceRegister(0xE000.toShort(), DeviceRegister.RegisterValueType.FLOAT),
        AMPLITUDE_COEFFICIENT to DeviceRegister(0x100C, DeviceRegister.RegisterValueType.FLOAT),
        FORM_COEFFICIENT to DeviceRegister(0x100E, DeviceRegister.RegisterValueType.FLOAT),
        AMPLITUDE_COEFFICIENT_TM to DeviceRegister(0x101C, DeviceRegister.RegisterValueType.FLOAT),
        FORM_COEFFICIENT_TM to DeviceRegister(0x101E, DeviceRegister.RegisterValueType.FLOAT),
        TIME_AVERAGING to DeviceRegister(0x10C8, DeviceRegister.RegisterValueType.FLOAT),
    )


    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
