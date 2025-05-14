package domain.repository

import domain.model.PortInfo

interface SerialPortRepository {
    fun getAvailablePorts(): List<PortInfo>
} 