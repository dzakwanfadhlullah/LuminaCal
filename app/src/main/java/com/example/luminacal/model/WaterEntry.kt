package com.example.luminacal.model

data class WaterEntry(
    val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class WaterState(
    val consumed: Int = 0,      // ml consumed today
    val target: Int = 2000,     // daily goal (2L default)
    val glassCount: Int = 0     // number of glasses (250ml each)
)
