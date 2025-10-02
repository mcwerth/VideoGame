package com.example.vaultofluck.data.repository

import com.example.vaultofluck.core.analytics.AnalyticsLogger
import com.example.vaultofluck.core.economy.Economy
import com.example.vaultofluck.core.time.GameClock
import com.example.vaultofluck.data.local.GameDatabase
import com.example.vaultofluck.data.local.entity.CurrencyEntity
import com.example.vaultofluck.data.local.entity.GeneratorEntity
import com.example.vaultofluck.data.local.entity.PlayerEntity
import com.example.vaultofluck.data.local.entity.QuestEntity
import com.example.vaultofluck.data.local.entity.RunEntity
import com.example.vaultofluck.data.local.entity.UpgradeEntity
import com.example.vaultofluck.data.serialization.GameJson
import com.example.vaultofluck.domain.model.Currency
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.model.DashboardState
import com.example.vaultofluck.domain.model.GachaItem
import com.example.vaultofluck.domain.model.GachaPullResult
import com.example.vaultofluck.domain.model.Generator
import com.example.vaultofluck.domain.model.PlayerSettings
import com.example.vaultofluck.domain.model.Quest
import com.example.vaultofluck.domain.model.QuestType
import com.example.vaultofluck.domain.model.Run
import com.example.vaultofluck.domain.model.RunStatus
import com.example.vaultofluck.domain.model.Upgrade
import com.example.vaultofluck.domain.model.UpgradeType
import com.example.vaultofluck.domain.util.IdleTick
import com.example.vaultofluck.domain.util.PrestigeResult
import com.example.vaultofluck.domain.util.RunUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlin.math.max

class GameRepositoryImpl(
    private val database: GameDatabase,
    private val clock: GameClock,
    private val analytics: AnalyticsLogger
) : GameRepository {

    private val playerDao = database.playerDao()
    private val currencyDao = database.currencyDao()
    private val generatorDao = database.generatorDao()
    private val upgradeDao = database.upgradeDao()
    private val historyDao = database.gachaHistoryDao()
    private val runDao = database.runDao()
    private val questDao = database.questDao()

    override suspend fun seedIfNeeded() {
        val existing = playerDao.getPlayer()
        if (existing != null) return
        val now = clock.nowMillis()
        val settings = PlayerSettings()
        playerDao.insert(
            PlayerEntity(
                createdAt = now,
                lastSeenAt = now,
                totalPrestiges = 0,
                settingsJson = GameJson.instance.encodeToString(settings)
            )
        )
        currencyDao.upsertAll(
            listOf(
                CurrencyEntity(CurrencyIds.COINS, 0.0),
                CurrencyEntity(CurrencyIds.GEMS, 0.0),
                CurrencyEntity(CurrencyIds.TOKENS, 10.0),
                CurrencyEntity(CurrencyIds.ESSENCE, 0.0)
            )
        )
        generatorDao.upsertAll(
            (1..10).map { tier ->
                val baseRate = Economy.GENERATOR_BASE_RATE * tier
                GeneratorEntity(
                    id = tier,
                    tier = tier,
                    level = if (tier == 1) 1 else 0,
                    baseRate = baseRate,
                    growth = Economy.GENERATOR_GROWTH,
                    multiplier = 1.0,
                    unlockCost = Economy.generatorUnlockCost(tier),
                    unlocked = tier == 1
                )
            }
        )
        upgradeDao.upsertAll(
            listOf(
                UpgradeEntity(1, UpgradeType.GENERATOR_RATE, 0, 50.0, 100, false),
                UpgradeEntity(2, UpgradeType.GLOBAL_MULTIPLIER, 0, 200.0, 50, false),
                UpgradeEntity(3, UpgradeType.AFK_BOOST, 0, 100.0, 25, false),
                UpgradeEntity(4, UpgradeType.AUTO_SPIN, 0, 250.0, 5, false),
                UpgradeEntity(5, UpgradeType.MULTI_PULL_DISCOUNT, 0, 150.0, 10, false),
                UpgradeEntity(6, UpgradeType.META_PRESTIGE, 0, 1000.0, 30, true)
            )
        )
        questDao.upsertAll(defaultQuests(now))
    }

    override fun dashboard(): Flow<DashboardState> {
        val currencies = currencies()
        val generators = generators()
        val upgrades = upgrades()
        val runFlow = runs().map { list -> list.firstOrNull { it.status == RunStatus.ONGOING } }
        return combine(currencies, generators, upgrades, runFlow) { curr, gens, ups, run ->
            DashboardState(
                currencies = curr,
                generators = gens,
                upgrades = ups,
                currentRun = run,
                settings = PlayerSettings() // replaced by observe below
            )
        }.combine(playerDao.observePlayer()) { state, player ->
            val settings = player?.settingsJson?.let { GameJson.instance.decodeFromString<PlayerSettings>(it) }
                ?: PlayerSettings()
            state.copy(settings = settings)
        }
    }

    override fun currencies(): Flow<List<Currency>> = currencyDao.observeCurrencies()
        .map { list -> list.map { Currency(it.name, it.amount) } }
        .distinctUntilChanged()

    override fun generators(): Flow<List<Generator>> = generatorDao.observeGenerators()
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()

    override fun upgrades(): Flow<List<Upgrade>> = upgradeDao.observeUpgrades()
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()

    override fun quests(): Flow<List<Quest>> = questDao.observeQuests()
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()

    override fun runs(): Flow<List<Run>> = runDao.recentRuns()
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()

    override fun gachaItems(): Flow<List<GachaItem>> = historyDao.recentHistory()
        .map { history ->
            val latest = history.firstOrNull() ?: return@map emptyList()
            GameJson.instance.decodeFromString<GachaPullResult>(latest.resultsJson).items
        }
        .distinctUntilChanged()

    override suspend fun getPlayerSettings(): PlayerSettings {
        val player = playerDao.getPlayer() ?: error("Player missing")
        return GameJson.instance.decodeFromString(player.settingsJson)
    }

    override suspend fun updatePlayerSettings(settings: PlayerSettings) {
        val player = playerDao.getPlayer() ?: error("Player missing")
        playerDao.update(player.copy(settingsJson = GameJson.instance.encodeToString(settings)))
    }

    override suspend fun updatePlayerLastSeen(timestamp: Long) {
        val player = playerDao.getPlayer() ?: error("Player missing")
        playerDao.update(player.copy(lastSeenAt = timestamp))
    }

    override suspend fun spendCurrency(name: String, amount: Double): Boolean {
        val currency = currencyDao.getCurrency(name) ?: return false
        if (currency.amount + 1e-6 < amount) return false
        currencyDao.update(currency.copy(amount = currency.amount - amount))
        return true
    }

    override suspend fun incrementCurrency(name: String, delta: Double) {
        currencyDao.increment(name, delta)
    }

    override suspend fun setCurrency(name: String, amount: Double) {
        currencyDao.upsertAll(listOf(CurrencyEntity(name, amount)))
    }

    override suspend fun updateGenerator(generator: Generator) {
        generatorDao.update(generator.toEntity())
    }

    override suspend fun updateUpgrade(upgrade: Upgrade) {
        upgradeDao.update(upgrade.toEntity())
    }

    override suspend fun recordGacha(pull: GachaPullResult) {
        val player = playerDao.getPlayer() ?: error("Player missing")
        historyDao.insert(
            com.example.vaultofluck.data.local.entity.GachaHistoryEntity(
                timestamp = clock.nowMillis(),
                pullSize = pull.items.size,
                resultsJson = GameJson.instance.encodeToString(pull),
                pityBefore = pull.pityBefore,
                pityAfter = pull.pityAfter
            )
        )
        val updatedSettings = player.settingsJson.let { GameJson.instance.decodeFromString<PlayerSettings>(it) }
            .copy(gachaPity = pull.pityAfter)
        playerDao.update(player.copy(settingsJson = GameJson.instance.encodeToString(updatedSettings)))
    }

    override suspend fun startRun(now: Long, depth: Int): RunUpdate {
        val existing = runDao.latestWithStatus(RunStatus.ONGOING)
        if (existing != null) {
            return RunUpdate(existing.toDomain(), wasNew = false)
        }
        val run = RunEntity(
            startedAt = now,
            endedAt = null,
            depth = depth,
            rewardGems = 0.0,
            status = RunStatus.ONGOING
        )
        val id = runDao.insert(run)
        val inserted = run.copy(id = id)
        analytics.logEvent("run_start", mapOf("depth" to depth))
        return RunUpdate(inserted.toDomain(), wasNew = true)
    }

    override suspend fun finishRun(success: Boolean, depth: Int, now: Long): RunUpdate {
        val active = runDao.latestWithStatus(RunStatus.ONGOING) ?: return RunUpdate(
            run = RunEntity(0, now, now, depth, 0.0, if (success) RunStatus.SUCCESS else RunStatus.FAILED).toDomain(),
            wasNew = false
        )
        val rewardGems = if (success) depth * 5.0 else depth * 2.0
        val rewardTokens = if (success) depth / 2.0 else depth / 4.0
        incrementCurrency(CurrencyIds.GEMS, rewardGems)
        incrementCurrency(CurrencyIds.TOKENS, rewardTokens)
        val updated = active.copy(
            endedAt = now,
            depth = depth,
            rewardGems = rewardGems,
            status = if (success) RunStatus.SUCCESS else RunStatus.FAILED
        )
        runDao.update(updated)
        analytics.logEvent("run_end", mapOf("success" to success, "depth" to depth))
        return RunUpdate(updated.toDomain(), wasNew = false)
    }

    override suspend fun currentRun(): Run? = runDao.latestWithStatus(RunStatus.ONGOING)?.toDomain()

    override suspend fun performPrestige(coinsSpent: Double, essenceGained: Long): PrestigeResult {
        val player = playerDao.getPlayer() ?: error("Player missing")
        spendCurrency(CurrencyIds.COINS, coinsSpent)
        incrementCurrency(CurrencyIds.ESSENCE, essenceGained.toDouble())
        val generators = generatorDao.getGenerators()
        generatorDao.upsertAll(generators.map { it.copy(level = if (it.tier == 1) 1 else 0, unlocked = it.tier == 1) })
        upgradeDao.upsertAll(upgradeDao.getAll().map { upgrade ->
            if (upgrade.isMeta) upgrade else upgrade.copy(level = 0)
        })
        currencyDao.upsertAll(
            currencyDao.getAll().map {
                when (it.name) {
                    CurrencyIds.COINS -> it.copy(amount = 0.0)
                    CurrencyIds.TOKENS -> it.copy(amount = max(10.0, it.amount))
                    CurrencyIds.GEMS -> it.copy(amount = 0.0)
                    else -> it
                }
            }
        )
        val newPrestiges = player.totalPrestiges + 1
        val settings = GameJson.instance.decodeFromString<PlayerSettings>(player.settingsJson)
        val updatedSettings = settings.copy(
            metaLevel = settings.metaLevel + 1,
            gachaPity = 0,
            softPityCounter = 0
        )
        playerDao.update(
            player.copy(
                totalPrestiges = newPrestiges,
                settingsJson = GameJson.instance.encodeToString(updatedSettings)
            )
        )
        analytics.logEvent("prestige", mapOf("essence" to essenceGained))
        return PrestigeResult(essenceGained = essenceGained, totalPrestiges = newPrestiges, coinsSpent = coinsSpent)
    }

    override suspend fun tickIdle(now: Long, offline: Boolean): IdleTick {
        val player = playerDao.getPlayer() ?: error("Player missing")
        val elapsed = ((now - player.lastSeenAt) / 1000).coerceAtLeast(0)
        if (elapsed == 0L) return IdleTick(0.0, 0)
        val generators = generatorDao.getGenerators().map { it.toDomain() }
        val upgrades = upgradeDao.getAll().map { it.toDomain() }
        val metaBoost = upgrades.firstOrNull { it.type == UpgradeType.META_PRESTIGE }?.level ?: 0
        val globalMultiplier = 1.0 + (upgrades.firstOrNull { it.type == UpgradeType.GLOBAL_MULTIPLIER }?.level ?: 0) * 0.05 + Economy.globalMultiplierFromMeta(metaBoost)
        val rate = generators.filter { it.unlocked }.sumOf { generator ->
            Economy.generatorRate(generator.tier, generator.level, generator.multiplier) * globalMultiplier
        }
        val afkBoost = 1.0 + (upgrades.firstOrNull { it.type == UpgradeType.AFK_BOOST }?.level ?: 0) * 0.1
        val earned = Economy.offlineEarnings(rate, elapsed, afkBoost)
        incrementCurrency(CurrencyIds.COINS, earned)
        updatePlayerLastSeen(now)
        return IdleTick(earned, elapsed)
    }

    override suspend fun claimQuest(id: Int): Quest? {
        val quest = questDao.get(id) ?: return null
        if (quest.claimed || quest.progress < quest.target) return quest.toDomain()
        questDao.update(quest.copy(claimed = true))
        incrementCurrency(CurrencyIds.TOKENS, quest.rewardTokens.toDouble())
        analytics.logEvent("quest_claim", mapOf("id" to id))
        return quest.copy(claimed = true).toDomain()
    }

    private fun GeneratorEntity.toDomain(): Generator = Generator(
        id = id,
        tier = tier,
        level = level,
        baseRate = baseRate,
        growth = growth,
        multiplier = multiplier,
        unlockCost = unlockCost,
        unlocked = unlocked
    )

    private fun Generator.toEntity(): GeneratorEntity = GeneratorEntity(
        id = id,
        tier = tier,
        level = level,
        baseRate = baseRate,
        growth = growth,
        multiplier = multiplier,
        unlockCost = unlockCost,
        unlocked = unlocked
    )

    private fun UpgradeEntity.toDomain(): Upgrade = Upgrade(
        id = id,
        type = type,
        level = level,
        cost = cost,
        maxLevel = maxLevel,
        isMeta = isMeta
    )

    private fun Upgrade.toEntity(): UpgradeEntity = UpgradeEntity(
        id = id,
        type = type,
        level = level,
        cost = cost,
        maxLevel = maxLevel,
        isMeta = isMeta
    )

    private fun QuestEntity.toDomain(): Quest = Quest(
        id = id,
        type = type,
        target = target,
        progress = progress,
        rewardTokens = rewardTokens,
        expiresAt = expiresAt,
        claimed = claimed
    )

    private fun RunEntity.toDomain(): Run = Run(
        id = id,
        startedAt = startedAt,
        endedAt = endedAt,
        depth = depth,
        rewardGems = rewardGems,
        status = status
    )

    private fun defaultQuests(now: Long): List<QuestEntity> {
        val dayMillis = 24 * 60 * 60 * 1000L
        return listOf(
            QuestEntity(1, QuestType.EARN_COINS, 1_000.0, 0.0, 5, now + dayMillis, false),
            QuestEntity(2, QuestType.SPIN_GACHA, 10.0, 0.0, 8, now + dayMillis, false),
            QuestEntity(3, QuestType.COMPLETE_RUN, 1.0, 0.0, 10, now + dayMillis, false),
            QuestEntity(101, QuestType.PRESTIGE, 1.0, 0.0, 25, now + dayMillis * 7, false),
            QuestEntity(102, QuestType.UPGRADE_GENERATOR, 15.0, 0.0, 20, now + dayMillis * 7, false),
            QuestEntity(103, QuestType.EARN_COINS, 1_000_000.0, 0.0, 40, now + dayMillis * 7, false)
        )
    }
}
