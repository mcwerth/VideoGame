package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.util.RunUpdate

/** Finalizes the current run and grants rewards. */
class EndRunUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(success: Boolean, depth: Int): GameResult<RunUpdate> {
        val now = System.currentTimeMillis()
        val update = repository.finishRun(success, depth, now)
        return GameResult.Success(update)
    }
}
