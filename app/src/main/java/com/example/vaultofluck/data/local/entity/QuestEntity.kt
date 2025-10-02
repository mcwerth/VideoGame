package com.example.vaultofluck.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vaultofluck.domain.model.QuestType

@Entity(tableName = "quest")
data class QuestEntity(
    @PrimaryKey val id: Int,
    val type: QuestType,
    val target: Double,
    val progress: Double,
    val rewardTokens: Int,
    val expiresAt: Long,
    val claimed: Boolean
)
