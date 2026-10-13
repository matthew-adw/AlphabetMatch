package com.example.alphabetmatch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "progress")

/**
 * Local-only progress storage (DataStore Preferences).
 *
 * COPPA / GDPR-K note: this stores only a level number, a star count and two
 * sound toggles on-device. No personal data, no identifiers, nothing leaves the device.
 */
class ProgressRepository(private val context: Context) {

    data class Progress(
        val level: Int,
        val stars: Int,
        val soundEnabled: Boolean,
        val musicEnabled: Boolean,
    )

    private object Keys {
        val LEVEL = intPreferencesKey("level")
        val STARS = intPreferencesKey("stars")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
    }

    val progress: Flow<Progress> = context.progressDataStore.data.map { prefs ->
        Progress(
            level = prefs[Keys.LEVEL] ?: 1,
            stars = prefs[Keys.STARS] ?: 0,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
        )
    }

    suspend fun setLevel(level: Int) {
        context.progressDataStore.edit { it[Keys.LEVEL] = level }
    }

    suspend fun setStars(stars: Int) {
        context.progressDataStore.edit { it[Keys.STARS] = stars }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.progressDataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.progressDataStore.edit { it[Keys.MUSIC_ENABLED] = enabled }
    }

    suspend fun resetProgress() {
        context.progressDataStore.edit { prefs ->
            prefs[Keys.LEVEL] = 1
            prefs[Keys.STARS] = 0
            // Sound/music toggles are intentionally kept.
        }
    }
}
