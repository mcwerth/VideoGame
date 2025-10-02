package com.example.vaultofluck.data.repository

import com.example.vaultofluck.domain.model.DashboardState
import com.example.vaultofluck.domain.model.GachaPullResult
import com.example.vaultofluck.domain.model.GachaItem
import com.example.vaultofluck.domain.model.Generator
import com.example.vaultofluck.domain.model.PlayerSettings
import com.example.vaultofluck.domain.model.Quest
import com.example.vaultofluck.domain.model.Run
import com.example.vaultofluck.domain.model.RunStatus
import com.example.vaultofluck.domain.model.Upgrade
import com.example.vaultofluck.domain.util.IdleTick
import com.example.vaultofluck.domain.util.PrestigeResult
import com.example.vaultofluck.domain.util.RunUpdate
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun seedIfNeeded()
    fun dashboard(): Flow<DashboardState>
    fun currencies(): Flow<List<com.example.vaultofluck.domain.model.Currency>>
    fun generators(): Flow<List<Generator>>
    fun upgrades(): Flow<List<Upgrade>>
    fun quests(): Flow<List<Quest>>
    fun runs(): Flow<List<Run>>
    fun gachaItems(): Flow<List<GachaItem>>
    suspend fun getPlayerSettings(): PlayerSettings
    suspend fun updatePlayerSettings(settings: PlayerSettings)
    suspend fun updatePlayerLastSeen(timestamp: Long)
    suspend fun spendCurrency(name: String, amount: Double): Boolean
    suspend fun incrementCurrency(name: String, delta: Double)
    suspend fun setCurrency(name: String, amount: Double)
    suspend fun updateGenerator(generator: Generator)
    suspend fun updateUpgrade(upgrade: Upgrade)
    suspend fun recordGacha(pull: GachaPullResult)
    suspend fun startRun(now: Long, depth: Int): RunUpdate
    suspend fun finishRun(success: Boolean, depth: Int, now: Long): RunUpdate
    suspend fun currentRun(): Run?
    suspend fun performPrestige(coinsSpent: Double, essenceGained: Long): PrestigeResult
    suspend fun tickIdle(now: Long, offline: Boolean): IdleTick
    suspend fun claimQuest(id: Int): Quest?
}
