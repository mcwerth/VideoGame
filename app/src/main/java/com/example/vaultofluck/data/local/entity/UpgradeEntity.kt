package com.example.vaultofluck.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vaultofluck.domain.model.UpgradeType

@Entity(tableName = "upgrade")
data class UpgradeEntity(
    @PrimaryKey val id: Int,
    val type: UpgradeType,
    val level: Int,
    val cost: Double,
    val maxLevel: Int,
    val isMeta: Boolean
)
