package com.example.alphabetmatch.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.alphabetmatch.R

/** Which looping background track should be playing. */
enum class MusicTrack(val resId: Int) {
    HOME(R.raw.music_home),
    GAME(R.raw.music_game),
}

/**
 * Tiny audio wrapper: [SoundPool] for short SFX, [MediaPlayer] for looping music.
 *
 * Music switches between a HOME track (home/settings) and a GAME track (play/reward).
 *
 * TODO(ASSETS): res/raw currently holds SILENT placeholder audio.
 *  Replace the .wav placeholders with real .ogg files (same base names) - see README.md.
 */
class SoundManager(context: Context) {

    private val appContext = context.applicationContext

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val sampleIds = mutableMapOf<Int, Int>()
    private var musicPlayer: MediaPlayer? = null
    private var currentTrack: MusicTrack = MusicTrack.HOME

    var soundEnabled: Boolean = true
    var musicEnabled: Boolean = true

    init {
        listOf(
            R.raw.sfx_correct,
            R.raw.sfx_try_again,
            R.raw.sfx_letter_a, R.raw.sfx_letter_b, R.raw.sfx_letter_c,
            R.raw.sfx_letter_d, R.raw.sfx_letter_e, R.raw.sfx_letter_f,
            R.raw.sfx_letter_g, R.raw.sfx_letter_h, R.raw.sfx_letter_i,
            R.raw.sfx_letter_j, R.raw.sfx_letter_k, R.raw.sfx_letter_l,
            R.raw.sfx_letter_m, R.raw.sfx_letter_n, R.raw.sfx_letter_o,
            R.raw.sfx_letter_p, R.raw.sfx_letter_q, R.raw.sfx_letter_r,
            R.raw.sfx_letter_s, R.raw.sfx_letter_t, R.raw.sfx_letter_u,
            R.raw.sfx_letter_v, R.raw.sfx_letter_w, R.raw.sfx_letter_x,
            R.raw.sfx_letter_y, R.raw.sfx_letter_z,
        ).forEach { resId -> sampleIds[resId] = soundPool.load(appContext, resId, 1) }
    }

    /** Plays a short sound effect (honours the sound toggle). */
    fun play(resId: Int) {
        if (!soundEnabled) return
        sampleIds[resId]?.let { sampleId -> soundPool.play(sampleId, 1f, 1f, 1, 0, 1f) }
    }

    /** Switches the looping background track (no-op if already the current one). */
    fun setMusicTrack(track: MusicTrack) {
        if (track == currentTrack) return
        currentTrack = track
        musicPlayer?.release()
        musicPlayer = null
        if (musicEnabled) startMusic()
    }

    /** Called whenever the music toggle changes. */
    fun updateMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        if (enabled) startMusic() else pauseMusic()
    }

    private fun startMusic() {
        if (!musicEnabled) return
        val player = musicPlayer ?: MediaPlayer.create(appContext, currentTrack.resId)?.apply {
            isLooping = true
            setVolume(0.35f, 0.35f)
        }.also { musicPlayer = it }
        if (player?.isPlaying == false) player.start()
    }

    private fun pauseMusic() {
        if (musicPlayer?.isPlaying == true) musicPlayer?.pause()
    }

    fun onAppBackground() = pauseMusic()
    fun onAppForeground() = startMusic()

    fun release() {
        soundPool.release()
        musicPlayer?.release()
        musicPlayer = null
    }
}
