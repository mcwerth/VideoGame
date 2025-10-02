package com.example.vaultofluck.ui.viewmodel

import com.example.vaultofluck.core.random.DefaultRng
import com.example.vaultofluck.domain.model.Currency
import com.example.vaultofluck.domain.model.CurrencyIds
import com.example.vaultofluck.domain.model.DashboardState
import com.example.vaultofluck.domain.model.PlayerSettings
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.usecase.ObserveGachaHistoryUseCase
import com.example.vaultofluck.domain.usecase.PullGachaUseCase
import com.example.vaultofluck.testing.FakeGameRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GachaViewModelTest {
    @Test
    fun pullPopulatesItems() = runTest {
        val repository = FakeGameRepository()
        repository.setDashboard(
            DashboardState(
                currencies = listOf(Currency(CurrencyIds.TOKENS, 100.0)),
                settings = PlayerSettings(gachaPity = 0)
            )
        )
        val viewModel = GachaViewModel(
            loadDashboard = LoadDashboardUseCase(repository),
            observeGachaHistory = ObserveGachaHistoryUseCase(repository),
            pullGacha = PullGachaUseCase(repository, DefaultRng(seed = 1))
        )
        advanceUntilIdle()
        viewModel.pull(1)
        advanceUntilIdle()
        assertThat(viewModel.state.value.lastItems).isNotEmpty()
    }
}
