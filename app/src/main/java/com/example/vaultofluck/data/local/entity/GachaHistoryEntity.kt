package com.example.vaultofluck.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gacha_history")
data class GachaHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val pullSize: Int,
    val resultsJson: String,
    val pityBefore: Int,
    val pityAfter: Int
)
