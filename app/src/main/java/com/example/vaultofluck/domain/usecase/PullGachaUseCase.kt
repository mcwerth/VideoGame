package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.economy.Economy
import com.example.vaultofluck.core.random.PityState
import com.example.vaultofluck.core.random.Rng
import com.example.vaultofluck.core.random.WeightedEntry
import com.example.vaultofluck.core.random.applyPity
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.model.GachaItem
import com.example.vaultofluck.domain.model.GachaPullResult
import com.example.vaultofluck.domain.util.GachaSummary
import kotlin.math.min

/**
 * Performs a gacha pull sequence with pity mechanics and odds disclosure.
 */
class PullGachaUseCase(
    private val repository: GameRepository,
    private val rng: Rng
) {
    suspend operator fun invoke(pullSize: Int): GameResult<GachaSummary> {
        if (pullSize !in setOf(1, 5, 10, 50)) {
            return GameResult.Error("Unsupported pull size")
        }
        val cost = Economy.multiPullCost(pullSize)
        val settings = repository.getPlayerSettings()
        if (!repository.spendCurrency(CurrencyIds.TOKENS, cost)) {
            return GameResult.Error("Not enough tokens")
        }
        var pity = settings.gachaPity
        val results = buildList {
            repeat(pullSize) {
                val adjustedWeights = applyPity(
                    base = Economy.rarityWeights,
                    pity = PityState(pullsSinceEpic = pity, pullsSinceLegendary = pity),
                    softStart = Economy.pitySoftStart,
                    hardCap = Economy.pityHardCap,
                    ramp = Economy.pityRamp
                )
                val rarity = rng.pickWeighted(adjustedWeights.map { WeightedEntry(it.key, it.value) })
                val pool = Economy.lootTable.getValue(rarity)
                val index = min((rng.nextDouble() * pool.size).toInt(), pool.lastIndex)
                add(GachaItem(pool[index], rarity))
                pity = if (rarity == "Epic" || rarity == "Legendary" || rarity == "Mythic") 0 else pity + 1
            }
        }
        val pull = GachaPullResult(
            items = results,
            pityBefore = settings.gachaPity,
            pityAfter = pity,
            tokensSpent = cost
        )
        repository.recordGacha(pull)
        val audit = rng.audit(Economy.rarityWeights.map { WeightedEntry(it.key, it.value) })
        return GameResult.Success(GachaSummary(pull = pull, audit = audit))
    }
}
