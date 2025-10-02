package com.example.vaultofluck.domain.usecase

import com.example.vaultofluck.core.analytics.AnalyticsLogger
import com.example.vaultofluck.core.result.GameResult
import com.example.vaultofluck.data.repository.GameRepositoryImpl
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.testing.FakeClock
import com.example.vaultofluck.testing.InMemoryDbRule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class DoPrestigeUseCaseTest {
    @get:Rule
    val dbRule = InMemoryDbRule()

    private lateinit var repository: GameRepositoryImpl
    private val analytics = object : AnalyticsLogger {
        override fun logEvent(name: String, attributes: Map<String, Any?>) {}
    }

    @Before
    fun setUp() = runBlocking {
        repository = GameRepositoryImpl(dbRule.database, FakeClock(0), analytics)
        repository.seedIfNeeded()
        repository.setCurrency(CurrencyIds.COINS, 1_000_000.0)
    }

    @Test
    fun prestigeGrantsEssence() = runBlocking {
        val useCase = DoPrestigeUseCase(repository)
        val result = useCase()
        assertThat(result).isInstanceOf(GameResult.Success::class.java)
        val essence = (result as GameResult.Success).data.essenceGained
        assertThat(essence).isGreaterThan(0L)
    }
}
