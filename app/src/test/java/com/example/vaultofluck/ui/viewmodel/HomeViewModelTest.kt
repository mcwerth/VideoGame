package com.example.vaultofluck.ui.viewmodel

import com.example.vaultofluck.domain.model.Currency
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.model.DashboardState
import com.example.vaultofluck.domain.model.Generator
import com.example.vaultofluck.domain.model.Run
import com.example.vaultofluck.domain.model.RunStatus
import com.example.vaultofluck.domain.util.IdleTick
import com.example.vaultofluck.domain.util.RunUpdate
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.usecase.StartRunUseCase
import com.example.vaultofluck.domain.usecase.TickIdleUseCase
import com.example.vaultofluck.testing.FakeGameRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class HomeViewModelTest {
    @Test
    fun stateReflectsDashboard() = runTest {
        val repository = FakeGameRepository()
        val generator = Generator(1, 1, 1, 1.0, 1.0, 1.0, 10.0, true)
        val run = Run(1, 0, null, 0, 0.0, RunStatus.ONGOING)
        repository.setDashboard(
            DashboardState(
                currencies = listOf(Currency(CurrencyIds.COINS, 100.0)),
                generators = listOf(generator),
                currentRun = run
            )
        )
        repository.tickResult = IdleTick(10.0, 60)
        repository.runUpdate = RunUpdate(run, wasNew = false)
        val viewModel = HomeViewModel(
            loadDashboard = LoadDashboardUseCase(repository),
            tickIdle = TickIdleUseCase(repository),
            startRun = StartRunUseCase(repository)
        )
        viewModel.onResume(60_000)
        val state = viewModel.state.value
        assertThat(state.currencies.first().amount).isEqualTo(100.0)
        assertThat(state.lastIdleTick?.coinsEarned).isEqualTo(10.0)
        assertThat(state.currentRun).isEqualTo(run)
    }
}
