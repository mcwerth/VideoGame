package com.example.vaultofluck.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vaultofluck.VaultOfLuckApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Applies offline progression regularly to keep idle gains synced when the player is away.
 */
class OfflineIncomeWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val container = VaultOfLuckApp.from(applicationContext).container
        return@withContext try {
            container.tickIdle.executeOffline()
            Result.success()
        } catch (t: Throwable) {
            Timber.e(t, "Offline worker failed")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "offline_income"
    }
}
