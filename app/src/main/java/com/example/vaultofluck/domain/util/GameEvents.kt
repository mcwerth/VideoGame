package com.example.vaultofluck.domain.util

import com.example.vaultofluck.domain.model.GachaPullResult
import com.example.vaultofluck.domain.model.Quest
import com.example.vaultofluck.domain.model.Run

/**
 * Result of an idle tick calculation.
 */
data class IdleTick(
    val coinsEarned: Double,
    val elapsedSeconds: Long
)

/**
 * Prestige summary returned after resetting the run.
 */
data class PrestigeResult(
    val essenceGained: Long,
    val totalPrestiges: Int,
    val coinsSpent: Double
)

/**
 * Snapshot used by the gacha screen to present the last pull.
 */
data class GachaSummary(
    val pull: GachaPullResult,
    val audit: Map<String, Double>
)

/**
 * Quest claim response containing the updated quest state.
 */
data class QuestClaimResult(
    val quest: Quest,
    val currenciesGranted: Map<String, Double>
)

/**
 * Active run update triggered when starting or ending a roguelite attempt.
 */
data class RunUpdate(
    val run: Run,
    val wasNew: Boolean
)
