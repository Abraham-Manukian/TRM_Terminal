package domain.repository

import domain.model.Register

interface RegisterRepository {
    fun getRegisters(): List<Register>
}