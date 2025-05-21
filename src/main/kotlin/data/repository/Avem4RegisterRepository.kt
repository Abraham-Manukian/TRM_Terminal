package data.repository

import data.device.Avem4Controller
import domain.model.Register
import domain.model.RegisterType
import domain.repository.RegisterRepository
import ru.avem.library.polling.DeviceRegister.RegisterValueType

class Avem4RegisterRepository(
    private val controller: Avem4Controller
) : RegisterRepository {

    override fun getRegisters(): List<Register> {
        return controller.model.registers.map { (id, deviceReg) ->
            Register(
                address = deviceReg.address.toInt(), // либо через as Number
                name = id,
                description = deviceReg.unit,
                type = when (deviceReg.valueType) {
                    RegisterValueType.FLOAT -> RegisterType.ANALOG
                    RegisterValueType.INT32 -> RegisterType.CONFIG
                    RegisterValueType.SHORT -> RegisterType.DISCRETE
                    else -> RegisterType.CONFIG
                },
                readOnly = true
            )
        }
    }
}
