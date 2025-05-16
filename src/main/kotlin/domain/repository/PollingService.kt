package org.example.domain.repository

import domain.model.PortConfig

interface PollingService {
    fun startPolling(config: PortConfig)
    fun stopAll()
    fun startFieldPolling(
        registerAddress: Int,
        onValue: (Double) -> Unit
    )
    fun writeRegister(
        registerAddress: Int,
        value: Number
    )
    fun getRegisters(): List<domain.model.Register>
}