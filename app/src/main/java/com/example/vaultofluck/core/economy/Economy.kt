package com.example.vaultofluck.core.economy

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Tuning knobs for the Vault of Luck economy. Centralizing balances enables quick iteration.
 */
object Economy {
    const val GENERATOR_BASE_COST = 10.0
    const val GENERATOR_GROWTH = 1.18
    const val GENERATOR_BASE_RATE = 1.5
    const val GLOBAL_RATE_GROWTH = 1.07
    const val PRESTIGE_K = 0.0005
    const val PRESTIGE_EXPONENT = 0.6
    const val OFFLINE_CAP_SECONDS = 60L * 60L * 8L // 8 hours

    val rarityWeights: Map<String, Double> = mapOf(
        "Common" to 0.60,
        "Rare" to 0.25,
        "Epic" to 0.10,
        "Legendary" to 0.04,
        "Mythic" to 0.01
    )

    val pitySoftStart = 20
    val pityHardCap = 60
    val pityRamp = 0.05

    val rarityNamesByWeightOrder = listOf("Common", "Rare", "Epic", "Legendary", "Mythic")

    val lootTable: Map<String, List<String>> = mapOf(
        "Common" to listOf("Coin Cache", "Minor Booster", "Spark"),
        "Rare" to listOf("Token Cache", "Gleaming Shard", "Run Rush"),
        "Epic" to listOf("Amplifier", "Auto-Spin Chip", "Lucky Charm"),
        "Legendary" to listOf("Chrono Core", "Void Key", "Jackpot Relic"),
        "Mythic" to listOf("Mythic Sigil", "Vaultheart", "Fate Anchor")
    )

    fun generatorUnlockCost(tier: Int): Double = GENERATOR_BASE_COST * tier.toDouble().pow(2.2)

    fun generatorLevelCost(tier: Int, level: Int): Double {
        val base = GENERATOR_BASE_COST * tier
        return base * GENERATOR_GROWTH.pow(level.toDouble())
    }

    fun generatorRate(tier: Int, level: Int, multiplier: Double): Double {
        val baseRate = GENERATOR_BASE_RATE * tier
        return baseRate * GENERATOR_GROWTH.pow(level.toDouble()) * multiplier
    }

    fun multiPullCost(pullSize: Int): Double {
        val discount = when (pullSize) {
            1 -> 1.0
            5 -> 4.8
            10 -> 9.3
            50 -> 45.0
            else -> pullSize.toDouble()
        }
        return discount
    }

    fun prestigeReturn(coins: Double): Long {
        if (coins <= 0) return 0
        return floor(PRESTIGE_K * coins.pow(PRESTIGE_EXPONENT)).toLong()
    }

    fun offlineEarnings(ratePerSecond: Double, elapsedSeconds: Long, boostMultiplier: Double): Double {
        val capped = min(elapsedSeconds, OFFLINE_CAP_SECONDS)
        return max(0.0, ratePerSecond * capped * boostMultiplier)
    }

    fun globalMultiplierFromMeta(metaLevel: Int): Double = 1.0 + ln(1 + metaLevel) * 0.25

    fun pityPreview(pullsSinceEpic: Int): Double {
        if (pullsSinceEpic < pitySoftStart) return rarityWeights.getValue("Epic")
        val ramp = 1 + (pullsSinceEpic - pitySoftStart + 1) * pityRamp
        return rarityWeights.getValue("Epic") * ramp
    }
}
