package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.economy.Economy
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.model.Generator
import kotlinx.coroutines.flow.first

/** Levels up a generator if the player can afford it. */
class UpgradeGeneratorUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(id: Int): GameResult<Generator> {
        val generators = repository.generators().first()
        val generator = generators.firstOrNull { it.id == id }
            ?: return GameResult.Error("Generator missing")
        val cost = if (generator.unlocked) {
            Economy.generatorLevelCost(generator.tier, generator.level + 1)
        } else {
            generator.unlockCost
        }
        if (!repository.spendCurrency(CurrencyIds.COINS, cost)) {
            return GameResult.Error("Not enough coins")
        }
        val updated = generator.copy(level = generator.level + 1, unlocked = true)
        repository.updateGenerator(updated)
        return GameResult.Success(updated)
    }
}
