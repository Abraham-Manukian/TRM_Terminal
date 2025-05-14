package domain.polling

import domain.model.PortConfig

interface PollingService {
    fun startPolling(config: PortConfig)
    fun stopAll()
    /** Теперь по адресам, а не по строковому id */
    fun startFieldPolling(
        registerAddress: Int,
        onValue: (Double) -> Unit
    )
    fun writeRegister(
        registerAddress: Int,
        value: Number
    )
}