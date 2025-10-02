package com.example.vaultofluck.testing

import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.Currency
import com.example.vaultofluck.domain.model.DashboardState
import com.example.vaultofluck.domain.model.GachaItem
import com.example.vaultofluck.domain.model.GachaPullResult
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
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGameRepository : GameRepository {
    private val dashboardFlow = MutableStateFlow(DashboardState())
    private val currencyFlow = MutableStateFlow<List<Currency>>(emptyList())
    private val generatorFlow = MutableStateFlow<List<Generator>>(emptyList())
    private val upgradeFlow = MutableStateFlow<List<Upgrade>>(emptyList())
    private val questFlow = MutableStateFlow<List<Quest>>(emptyList())
    private val runFlow = MutableStateFlow<List<Run>>(emptyList())
    private val gachaItemsFlow = MutableStateFlow<List<GachaItem>>(emptyList())

    var tickResult: IdleTick = IdleTick(0.0, 0)
    var runUpdate: RunUpdate = RunUpdate(Run(0, 0, null, 0, 0.0, RunStatus.ONGOING), wasNew = true)

    fun setDashboard(state: DashboardState) { dashboardFlow.value = state }
    fun setCurrencies(currencies: List<Currency>) { currencyFlow.value = currencies }

    override suspend fun seedIfNeeded() {}
    override fun dashboard(): Flow<DashboardState> = dashboardFlow
    override fun currencies(): Flow<List<Currency>> = currencyFlow
    override fun generators(): Flow<List<Generator>> = generatorFlow
    override fun upgrades(): Flow<List<Upgrade>> = upgradeFlow
    override fun quests(): Flow<List<Quest>> = questFlow
    override fun runs(): Flow<List<Run>> = runFlow
    override fun gachaItems(): Flow<List<GachaItem>> = gachaItemsFlow
    override suspend fun getPlayerSettings(): PlayerSettings = PlayerSettings()
    override suspend fun updatePlayerSettings(settings: PlayerSettings) {}
    override suspend fun updatePlayerLastSeen(timestamp: Long) {}
    override suspend fun spendCurrency(name: String, amount: Double): Boolean = true
    override suspend fun incrementCurrency(name: String, delta: Double) {}
    override suspend fun setCurrency(name: String, amount: Double) {}
    override suspend fun updateGenerator(generator: Generator) {}
    override suspend fun updateUpgrade(upgrade: Upgrade) {}
    override suspend fun recordGacha(pull: GachaPullResult) { gachaItemsFlow.value = pull.items }
    override suspend fun startRun(now: Long, depth: Int): RunUpdate = runUpdate
    override suspend fun finishRun(success: Boolean, depth: Int, now: Long): RunUpdate = runUpdate
    override suspend fun currentRun(): Run? = runUpdate.run
    override suspend fun performPrestige(coinsSpent: Double, essenceGained: Long): PrestigeResult =
        PrestigeResult(essenceGained, 1, coinsSpent)
    override suspend fun tickIdle(now: Long, offline: Boolean): IdleTick = tickResult
    override suspend fun claimQuest(id: Int): Quest? = questFlow.value.firstOrNull { it.id == id }
}
