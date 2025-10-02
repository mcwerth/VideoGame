package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.model.Upgrade
import kotlin.math.pow
import kotlinx.coroutines.flow.first

/**
 * Attempts to purchase or level up an upgrade.
 */
class ApplyUpgradeUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(upgradeId: Int): GameResult<Upgrade> {
        val upgrades = repository.upgrades().first()
        val upgrade = upgrades.firstOrNull { it.id == upgradeId }
            ?: return GameResult.Error("Upgrade missing")
        if (upgrade.level >= upgrade.maxLevel) {
            return GameResult.Error("Upgrade maxed")
        }
        val cost = upgrade.cost * 1.15.pow(upgrade.level.toDouble())
        if (!repository.spendCurrency(CurrencyIds.COINS, cost)) {
            return GameResult.Error("Not enough coins")
        }
        val updated = upgrade.copy(level = upgrade.level + 1)
        repository.updateUpgrade(updated)
        return GameResult.Success(updated)
    }
}
