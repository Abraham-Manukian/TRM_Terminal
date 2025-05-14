package org.example.domain.usecase

import domain.model.PortConfig
import domain.repository.ModbusRepository

//class ReadHoldingUseCase(private val repo: ModbusRepository) {
//    suspend operator fun invoke(
//        config: PortConfig,
//        slave: Byte,
//        address: Short,
//        count: Int
//    ): List<Short> = repo.sendReadHolding(config, slave, address, count)
//}