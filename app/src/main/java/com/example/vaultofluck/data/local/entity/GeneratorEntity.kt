package com.example.vaultofluck.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generator")
data class GeneratorEntity(
    @PrimaryKey val id: Int,
    val tier: Int,
    val level: Int,
    val baseRate: Double,
    val growth: Double,
    val multiplier: Double,
    val unlockCost: Double,
    val unlocked: Boolean
)
