package com.example.vaultofluck.domain.model

import kotlinx.serialization.Serializable
import kotlin.math.pow

/** Currency bag entry. */
@Serializable
data class Currency(
    val name: String,
    val amount: Double
)

/** Generator metadata used to compute idle rates. */
@Serializable
data class Generator(
    val id: Int,
    val tier: Int,
    val level: Int,
    val baseRate: Double,
    val growth: Double,
    val multiplier: Double,
    val unlockCost: Double,
    val unlocked: Boolean
) {
    val ratePerSecond: Double get() = baseRate * growth.pow(level.toDouble())
}

/** Global upgrade taxonomy. */
enum class UpgradeType {
    GENERATOR_RATE,
    GLOBAL_MULTIPLIER,
    AUTO_SPIN,
    AFK_BOOST,
    ODDS_REVEAL,
    CRIT_CHANCE,
    MULTI_PULL_DISCOUNT,
    META_PRESTIGE
}

/** Upgrade state record. */
@Serializable
data class Upgrade(
    val id: Int,
    val type: UpgradeType,
    val level: Int,
    val cost: Double,
    val maxLevel: Int,
    val isMeta: Boolean
)

enum class RunStatus { ONGOING, SUCCESS, FAILED }

@Serializable
data class Run(
    val id: Long,
    val startedAt: Long,
    val endedAt: Long?,
    val depth: Int,
    val rewardGems: Double,
    val status: RunStatus
)

enum class QuestType {
    EARN_COINS,
    SPIN_GACHA,
    COMPLETE_RUN,
    PRESTIGE,
    UPGRADE_GENERATOR
}

@Serializable
data class Quest(
    val id: Int,
    val type: QuestType,
    val target: Double,
    val progress: Double,
    val rewardTokens: Int,
    val expiresAt: Long,
    val claimed: Boolean
)

/** Loot table entry returned from gacha pulls. */
@Serializable
data class GachaItem(
    val name: String,
    val rarity: String
)

@Serializable
data class GachaPullResult(
    val items: List<GachaItem>,
    val pityBefore: Int,
    val pityAfter: Int,
    val tokensSpent: Double
)

@Serializable
data class PlayerSettings(
    val tutorialComplete: Boolean = false,
    val gachaPity: Int = 0,
    val metaLevel: Int = 0,
    val softPityCounter: Int = 0
)

/** Aggregate snapshot used across the home screen. */
data class DashboardState(
    val currencies: List<Currency> = emptyList(),
    val generators: List<Generator> = emptyList(),
    val upgrades: List<Upgrade> = emptyList(),
    val currentRun: Run? = null,
    val settings: PlayerSettings = PlayerSettings()
)
