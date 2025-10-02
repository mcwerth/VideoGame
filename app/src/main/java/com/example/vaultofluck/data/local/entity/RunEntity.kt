package com.example.vaultofluck.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vaultofluck.domain.model.RunStatus

@Entity(tableName = "run")
data class RunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,
    val endedAt: Long?,
    val depth: Int,
    val rewardGems: Double,
    val status: RunStatus
)
