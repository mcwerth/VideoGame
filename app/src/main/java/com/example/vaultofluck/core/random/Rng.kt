package com.example.vaultofluck.core.random

import kotlin.math.min
import kotlin.random.Random

/** Represents a single weighted entry. */
data class WeightedEntry<T>(val value: T, val weight: Double)

/**
 * Deterministic RNG abstraction that allows seeding and auditing of odds.
 */
interface Rng {
    /** Returns a uniformly distributed value in [0,1). */
    fun nextDouble(): Double

    /** Picks a single entry using roulette-wheel selection. */
    fun <T> pickWeighted(entries: List<WeightedEntry<T>>): T

    /** Creates a diagnostic snapshot of the provided entries. */
    fun <T> audit(entries: List<WeightedEntry<T>>, samples: Int = 1_000): Map<T, Double>
}

/** Default RNG backed by [Random]. */
class DefaultRng(seed: Long? = null) : Rng {
    private val random: Random = seed?.let { Random(it) } ?: Random(System.currentTimeMillis())

    override fun nextDouble(): Double = random.nextDouble()

    override fun <T> pickWeighted(entries: List<WeightedEntry<T>>): T {
        require(entries.isNotEmpty()) { "Entries must not be empty" }
        val totalWeight = entries.sumOf { it.weight.coerceAtLeast(0.0) }
        require(totalWeight > 0) { "Total weight must be > 0" }
        var roll = nextDouble() * totalWeight
        for (entry in entries) {
            roll -= entry.weight
            if (roll <= 0) return entry.value
        }
        return entries.last().value
    }

    override fun <T> audit(entries: List<WeightedEntry<T>>, samples: Int): Map<T, Double> {
        val counts = entries.associate { it.value to 0L }.toMutableMap()
        repeat(samples) {
            val pick = pickWeighted(entries)
            counts[pick] = counts.getValue(pick) + 1
        }
        return counts.mapValues { it.value.toDouble() / samples }
    }
}

/**
 * Encapsulates pity tracking logic for gacha pulls.
 */
data class PityState(
    val pullsSinceEpic: Int = 0,
    val pullsSinceLegendary: Int = 0
)

/**
 * Computes adjusted weights given pity thresholds.
 */
fun applyPity(
    base: Map<String, Double>,
    pity: PityState,
    softStart: Int,
    hardCap: Int,
    ramp: Double
): Map<String, Double> {
    if (pity.pullsSinceEpic + 1 >= hardCap) {
        return base.mapValues { (rarity, weight) ->
            if (rarity == "Epic" || rarity == "Legendary" || rarity == "Mythic") 1.0 else 0.0
        }
    }
    if (pity.pullsSinceEpic + 1 < softStart) return base
    val multiplier = 1 + min((pity.pullsSinceEpic + 1 - softStart + 1) * ramp, 3.0)
    return base.mapValues { (rarity, weight) ->
        when (rarity) {
            "Epic" -> weight * multiplier
            "Legendary" -> weight * (multiplier * 1.25)
            "Mythic" -> weight * (multiplier * 1.5)
            else -> weight * (1 - ramp)
        }.coerceAtLeast(0.0)
    }
}
