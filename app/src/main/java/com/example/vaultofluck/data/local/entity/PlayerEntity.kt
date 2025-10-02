package com.example.vaultofluck.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey val id: Int = 0,
    val createdAt: Long,
    val lastSeenAt: Long,
    val totalPrestiges: Int,
    val settingsJson: String
)
