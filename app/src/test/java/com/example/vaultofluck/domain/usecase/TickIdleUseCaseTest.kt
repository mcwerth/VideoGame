package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.analytics.AnalyticsLogger
import com.example.vaultofluck.data.repository.GameRepositoryImpl
import com.example.vaultofluck.testing.FakeClock
import com.example.vaultofluck.testing.InMemoryDbRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class TickIdleUseCaseTest {
    @get:Rule
    val dbRule = InMemoryDbRule()

    private lateinit var repository: GameRepositoryImpl
    private lateinit var clock: FakeClock

    private val analytics = object : AnalyticsLogger {
        override fun logEvent(name: String, attributes: Map<String, Any?>) {}
    }

    @Before
    fun setUp() = runBlocking {
        clock = FakeClock(0)
        repository = GameRepositoryImpl(dbRule.database, clock, analytics)
        repository.seedIfNeeded()
    }

    @Test
    fun tickIdleGrantsCoins() = runBlocking {
        clock.now = 0
        repository.updatePlayerLastSeen(0)
        val generators = repository.generators().first()
        repository.updateGenerator(generators.first().copy(level = 5))
        val result = repository.tickIdle(now = 60_000, offline = true)
        assertThat(result.coinsEarned).isGreaterThan(0.0)
    }
}
