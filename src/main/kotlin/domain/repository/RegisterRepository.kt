package domain.repository

import domain.model.Register

interface RegisterRepository {
    fun getAllRegisters(): List<Register>
    fun getSelectedRegisters(): List<Register>
    fun toggleRegisterSelection(address: Int)
    fun updateRegisterValue(address: Int, newValue: String)
}