package com.example.vaultofluck.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Player-tweakable preferences managed via DataStore.
 */
class GamePreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val animationsEnabled = booleanPreferencesKey("animations_enabled")
        val sfxVolume = floatPreferencesKey("sfx_volume")
        val largeText = booleanPreferencesKey("large_text")
        val debugSeed = intPreferencesKey("debug_seed")
    }

    val preferences: Flow<GamePreferences> = dataStore.data.map { prefs ->
        GamePreferences(
            animationsEnabled = prefs[Keys.animationsEnabled] ?: true,
            sfxVolume = prefs[Keys.sfxVolume] ?: 0.8f,
            largeText = prefs[Keys.largeText] ?: false,
            debugSeed = prefs[Keys.debugSeed]
        )
    }

    suspend fun updateAnimations(enabled: Boolean) {
        dataStore.edit { it[Keys.animationsEnabled] = enabled }
    }

    suspend fun updateLargeText(enabled: Boolean) {
        dataStore.edit { it[Keys.largeText] = enabled }
    }

    suspend fun updateSfxVolume(volume: Float) {
        dataStore.edit { it[Keys.sfxVolume] = volume }
    }

    suspend fun updateDebugSeed(seed: Int?) {
        dataStore.edit {
            if (seed == null) {
                it.remove(Keys.debugSeed)
            } else {
                it[Keys.debugSeed] = seed
            }
        }
    }
}

/** Human-readable snapshot of stored preferences. */
data class GamePreferences(
    val animationsEnabled: Boolean,
    val sfxVolume: Float,
    val largeText: Boolean,
    val debugSeed: Int?
)
