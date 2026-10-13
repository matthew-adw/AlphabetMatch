package com.example.alphabetmatch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alphabetmatch.R
import com.example.alphabetmatch.audio.MusicTrack
import com.example.alphabetmatch.audio.SoundManager
import com.example.alphabetmatch.data.ProgressRepository
import com.example.alphabetmatch.data.Round
import com.example.alphabetmatch.data.RoundGenerator
import com.example.alphabetmatch.data.WordBank
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen { Home, Game, Reward, Settings }

enum class Feedback { None, Correct, Wrong }

/** MVVM-lite: one shared ViewModel drives all four screens. */
class AlphabetViewModel(application: Application) : AndroidViewModel(application) {

    private val progressRepository = ProgressRepository(application)
    private val soundManager = SoundManager(application)

    data class UiState(
        val screen: AppScreen = AppScreen.Home,
        /** 1-based dopamine level counter (rounds are random / endless). */
        val level: Int = 1,
        val stars: Int = 0,
        val soundEnabled: Boolean = true,
        val musicEnabled: Boolean = true,
        /** The current randomly generated round (3 letters ↔ 3 pictures). */
        val round: Round = RoundGenerator.generate(),
        /** Completed connections in this round: (leftIndex, rightIndex). */
        val matched: List<Pair<Int, Int>> = emptyList(),
        /** Wrong connections made in the current level; 3 → restart at level 1. */
        val mistakes: Int = 0,
        /** Letters the child has matched, for reward-screen stickers. */
        val completedLetters: List<Char> = emptyList(),
        val feedback: Feedback = Feedback.None,
        /** Increments on every wrong connection so the shake animation restarts. */
        val shakeKey: Int = 0,
        /** Right-column index to shake (the wrongly connected picture), if any. */
        val wrongRightIndex: Int? = null,
        /** Transient full-width notice (e.g. "Back to Level 1!"), cleared after a delay. */
        val notice: String? = null,
        val showExitDialog: Boolean = false,
        /** Last 5 completed letters shown as stickers on the reward screen. */
        val stickerLetters: List<Char> = emptyList(),
        /** Stars total when the reward screen was reached (multiple of 5). */
        val milestoneStars: Int = 0,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                soundManager.soundEnabled = progress.soundEnabled
                soundManager.updateMusicEnabled(progress.musicEnabled)
                _uiState.update {
                    it.copy(
                        level = progress.level,
                        stars = progress.stars,
                        soundEnabled = progress.soundEnabled,
                        musicEnabled = progress.musicEnabled,
                    )
                }
            }
        }
    }

    private fun setMusicFor(screen: AppScreen) {
        soundManager.setMusicTrack(
            if (screen == AppScreen.Game || screen == AppScreen.Reward) MusicTrack.GAME
            else MusicTrack.HOME
        )
    }

    // ---- Navigation ----

    fun startGame() {
        _uiState.update {
            it.copy(
                screen = AppScreen.Game,
                feedback = Feedback.None,
                showExitDialog = false,
                round = RoundGenerator.generate(),
                matched = emptyList(),
                mistakes = 0,
                wrongRightIndex = null,
                notice = null,
            )
        }
        setMusicFor(AppScreen.Game)
    }

    fun openSettings() {
        _uiState.update { it.copy(screen = AppScreen.Settings) }
        setMusicFor(AppScreen.Settings)
    }

    fun goHome() {
        _uiState.update {
            it.copy(
                screen = AppScreen.Home,
                feedback = Feedback.None,
                showExitDialog = false,
                wrongRightIndex = null,
                notice = null,
            )
        }
        setMusicFor(AppScreen.Home)
    }

    fun continueFromReward() {
        _uiState.update {
            it.copy(
                screen = AppScreen.Game,
                feedback = Feedback.None,
                round = RoundGenerator.generate(),
                matched = emptyList(),
                mistakes = 0,
                wrongRightIndex = null,
            )
        }
        setMusicFor(AppScreen.Game)
    }

    fun requestExitGame() = _uiState.update { it.copy(showExitDialog = true) }
    fun dismissExitDialog() = _uiState.update { it.copy(showExitDialog = false) }

    // ---- Gameplay ----

    /**
     * Called when the child connects a left letter (index [leftIndex]) to a right
     * picture (index [rightIndex]) via drag or tap. Validates and updates state.
     * Ignores connections that reuse an already-matched letter or picture.
     */
    fun attemptConnect(leftIndex: Int, rightIndex: Int) {
        val state = _uiState.value
        if (state.screen != AppScreen.Game) return
        if (state.matched.any { it.first == leftIndex || it.second == rightIndex }) return

        if (state.round.matches(leftIndex, rightIndex)) {
            val newMatched = state.matched + (leftIndex to rightIndex)
            soundManager.play(R.raw.sfx_correct)
            soundManager.play(WordBank.letterSound(state.round.leftLetters[leftIndex]))
            _uiState.update {
                it.copy(matched = newMatched, feedback = Feedback.Correct, wrongRightIndex = null)
            }
            if (newMatched.size == RoundGenerator.PAIRS) {
                viewModelScope.launch { delay(700); completeLevel() }
            } else {
                viewModelScope.launch {
                    delay(800)
                    _uiState.update { s -> if (s.feedback == Feedback.Correct) s.copy(feedback = Feedback.None) else s }
                }
            }
        } else {
            val newMistakes = state.mistakes + 1
            soundManager.play(R.raw.sfx_try_again)
            if (newMistakes >= MAX_MISTAKES) {
                restartAtLevelOne()
            } else {
                _uiState.update {
                    it.copy(
                        mistakes = newMistakes,
                        feedback = Feedback.Wrong,
                        wrongRightIndex = rightIndex,
                        shakeKey = it.shakeKey + 1,
                    )
                }
                viewModelScope.launch {
                    delay(900)
                    _uiState.update { s ->
                        if (s.feedback == Feedback.Wrong) s.copy(feedback = Feedback.None, wrongRightIndex = null) else s
                    }
                }
            }
        }
    }

    /** A full round was matched: +1 star, +1 level, new round (or reward screen). */
    private fun completeLevel() {
        val state = _uiState.value
        val newStars = state.stars + 1
        val newLevel = state.level + 1
        val newCompleted = state.completedLetters + state.round.leftLetters
        val reachedReward = newStars % REWARD_EVERY_STARS == 0

        viewModelScope.launch {
            progressRepository.setStars(newStars)
            progressRepository.setLevel(newLevel)
        }
        _uiState.update {
            it.copy(
                stars = newStars,
                level = newLevel,
                matched = emptyList(),
                mistakes = 0,
                round = RoundGenerator.generate(),
                feedback = Feedback.None,
                wrongRightIndex = null,
                completedLetters = newCompleted,
                screen = if (reachedReward) AppScreen.Reward else AppScreen.Game,
                milestoneStars = if (reachedReward) newStars else it.milestoneStars,
                stickerLetters = if (reachedReward) newCompleted.takeLast(5) else it.stickerLetters,
                showExitDialog = false,
            )
        }
        setMusicFor(if (reachedReward) AppScreen.Reward else AppScreen.Game)
    }

    /** 3 wrong connections in a level → gently start over at level 1 (stars kept). */
    private fun restartAtLevelOne() {
        _uiState.update {
            it.copy(
                level = 1,
                mistakes = 0,
                matched = emptyList(),
                round = RoundGenerator.generate(),
                feedback = Feedback.None,
                wrongRightIndex = null,
                notice = "Back to Level 1!",
            )
        }
        viewModelScope.launch { progressRepository.setLevel(1) }
        viewModelScope.launch {
            delay(1_600)
            _uiState.update { s -> if (s.notice != null) s.copy(notice = null) else s }
        }
    }

    // ---- Settings ----

    fun toggleSound() {
        val enabled = !_uiState.value.soundEnabled
        _uiState.update { it.copy(soundEnabled = enabled) }
        soundManager.soundEnabled = enabled
        viewModelScope.launch { progressRepository.setSoundEnabled(enabled) }
    }

    fun toggleMusic() {
        val enabled = !_uiState.value.musicEnabled
        _uiState.update { it.copy(musicEnabled = enabled) }
        soundManager.updateMusicEnabled(enabled)
        viewModelScope.launch { progressRepository.setMusicEnabled(enabled) }
    }

    fun resetProgress() {
        viewModelScope.launch { progressRepository.resetProgress() }
        _uiState.update {
            it.copy(
                level = 1,
                stars = 0,
                matched = emptyList(),
                mistakes = 0,
                round = RoundGenerator.generate(),
                feedback = Feedback.None,
                wrongRightIndex = null,
                notice = null,
            )
        }
    }

    // ---- Lifecycle ----

    fun onAppBackground() = soundManager.onAppBackground()
    fun onAppForeground() = soundManager.onAppForeground()

    override fun onCleared() {
        soundManager.release()
        super.onCleared()
    }

    companion object {
        /** Wrong connections allowed in a level before restarting at level 1. */
        const val MAX_MISTAKES = 3

        /** Reward screen shown every N stars (= every N completed rounds). */
        const val REWARD_EVERY_STARS = 5

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                AlphabetViewModel(app)
            }
        }
    }
}

