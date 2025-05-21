package domain.usecase

import domain.model.Register
import domain.repository.RegisterRepository

class GetRegisterByAddressUseCase(
    private val repository: RegisterRepository
) {
    operator fun invoke(address: Int): Register {
        return repository.getRegisters().firstOrNull { it.address == address }
            ?: error("Регистр 0x${address.toString(16)} не найден")
    }
}
