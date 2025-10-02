package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.domain.util.IdleTick

/**
 * Applies idle earnings both on foreground resume and in offline worker runs.
 */
class TickIdleUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(now: Long): IdleTick = repository.tickIdle(now, offline = false)

    suspend fun executeOffline(): IdleTick = repository.tickIdle(System.currentTimeMillis(), offline = true)
}
