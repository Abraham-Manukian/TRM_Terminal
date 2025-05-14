package data.repository

import data.register.preloadFromProtocol
import domain.model.Register
import domain.repository.RegisterRepository

class RegisterRepositoryImpl : RegisterRepository {
    private val all = preloadFromProtocol() // загрузка из Excel, JSON, вручную
    private val selected = mutableSetOf<Int>()
    private val values = mutableMapOf<Int, String>()

    override fun getAllRegisters(): List<Register> = all
    override fun getSelectedRegisters(): List<Register> = all.filter { it.address in selected }
    override fun toggleRegisterSelection(address: Int) {
        if (selected.contains(address)) selected.remove(address) else selected.add(address)
    }
    override fun updateRegisterValue(address: Int, newValue: String) {
        values[address] = newValue
    }
}