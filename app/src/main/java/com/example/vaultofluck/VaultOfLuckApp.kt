package com.example.vaultofluck

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vaultofluck.core.analytics.AnalyticsLogger
import com.example.vaultofluck.core.analytics.LogcatAnalyticsLogger
import com.example.vaultofluck.core.random.DefaultRng
import com.example.vaultofluck.core.random.Rng
import com.example.vaultofluck.core.time.RealClock
import com.example.vaultofluck.core.work.OfflineIncomeWorker
import com.example.vaultofluck.data.local.GameDatabase
import com.example.vaultofluck.data.prefs.GamePreferencesDataSource
import com.example.vaultofluck.data.repository.GameRepository
import com.example.vaultofluck.data.repository.GameRepositoryImpl
import com.example.vaultofluck.domain.usecase.ApplyUpgradeUseCase
import com.example.vaultofluck.domain.usecase.ClaimQuestUseCase
import com.example.vaultofluck.domain.usecase.DoPrestigeUseCase
import com.example.vaultofluck.domain.usecase.EndRunUseCase
import com.example.vaultofluck.domain.usecase.LoadDashboardUseCase
import com.example.vaultofluck.domain.usecase.ObserveGachaHistoryUseCase
import com.example.vaultofluck.domain.usecase.ObserveQuestsUseCase
import com.example.vaultofluck.domain.usecase.PullGachaUseCase
import com.example.vaultofluck.domain.usecase.SaveGameSnapshotUseCase
import com.example.vaultofluck.domain.usecase.StartRunUseCase
import com.example.vaultofluck.domain.usecase.TickIdleUseCase
import com.example.vaultofluck.domain.usecase.UpgradeGeneratorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Application entry point wiring the lightweight service locator used throughout the game.
 */
class VaultOfLuckApp : Application() {

    lateinit var container: AppContainer
        private set

    private val Application.dataStore by preferencesDataStore(name = "vault_of_luck")

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val database = GameDatabase.build(this)
        val rng: Rng = DefaultRng()
        val analytics: AnalyticsLogger = LogcatAnalyticsLogger()
        val preferences = GamePreferencesDataSource(dataStore)
        val repository: GameRepository = GameRepositoryImpl(
            database = database,
            clock = RealClock,
            analytics = analytics
        )
        container = AppContainer(
            repository = repository,
            rng = rng,
            analytics = analytics,
            preferences = preferences
        )
        scheduleOfflineIncome()
        seedIfNecessary()
    }

    private fun scheduleOfflineIncome() {
        val workRequest = PeriodicWorkRequestBuilder<OfflineIncomeWorker>(
            15, TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            OfflineIncomeWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun seedIfNecessary() {
        CoroutineScope(Dispatchers.IO).launch {
            container.repository.seedIfNeeded()
        }
    }

    companion object {
        fun from(context: Context): VaultOfLuckApp =
            context.applicationContext as VaultOfLuckApp
    }
}

/**
 * Lightweight dependency container. In a production project we would rely on proper DI,
 * however manual wiring keeps the sample focused on gameplay architecture.
 */
class AppContainer(
    val repository: GameRepository,
    val rng: Rng,
    val analytics: AnalyticsLogger,
    val preferences: GamePreferencesDataSource
) {
    val tickIdle = TickIdleUseCase(repository)
    val pullGacha = PullGachaUseCase(repository, rng)
    val applyUpgrade = ApplyUpgradeUseCase(repository)
    val upgradeGenerator = UpgradeGeneratorUseCase(repository)
    val startRun = StartRunUseCase(repository)
    val endRun = EndRunUseCase(repository)
    val doPrestige = DoPrestigeUseCase(repository)
    val claimQuest = ClaimQuestUseCase(repository)
    val saveSnapshot = SaveGameSnapshotUseCase(repository)
    val loadDashboard = LoadDashboardUseCase(repository)
    val observeGacha = ObserveGachaHistoryUseCase(repository)
    val observeQuests = ObserveQuestsUseCase(repository)
}
