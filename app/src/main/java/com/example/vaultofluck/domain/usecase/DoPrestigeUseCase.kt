package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.economy.Economy
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.util.PrestigeResult
import kotlinx.coroutines.flow.first

/** Converts soft currency into essence and resets progression. */
class DoPrestigeUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): GameResult<PrestigeResult> {
        val currencies = repository.currencies().first()
        val coins = currencies.firstOrNull { it.name == CurrencyIds.COINS }?.amount ?: 0.0
        val essence = Economy.prestigeReturn(coins)
        if (essence <= 0) {
            return GameResult.Error("Insufficient coins for prestige")
        }
        val result = repository.performPrestige(coins, essence)
        return GameResult.Success(result)
    }
}
