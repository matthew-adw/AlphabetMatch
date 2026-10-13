package com.example.alphabetmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.alphabetmatch.ui.AlphabetViewModel
import com.example.alphabetmatch.ui.AppScreen
import com.example.alphabetmatch.ui.screens.GameScreen
import com.example.alphabetmatch.ui.screens.HomeScreen
import com.example.alphabetmatch.ui.screens.RewardScreen
import com.example.alphabetmatch.ui.screens.SettingsScreen
import com.example.alphabetmatch.ui.theme.AlphabetMatchTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AlphabetViewModel by viewModels { AlphabetViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TODO(ADS): when you add AdMob, run this BEFORE loading any ad, and keep these
        //  exact flags (COPPA / GDPR-K / EU & UK kids-app rules):
        //  MobileAds.setRequestConfiguration(
        //      RequestConfiguration.Builder()
        //          .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)   // Child-directed: ON
        //          .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)            // under-DC consent: ON
        //          .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)                           // Content rating: max "G"
        //          .build()
        //  )
        //  Also: request NON-personalized ads only, collect no identifiers, add the
        //  INTERNET permission only when the SDK lands, and never place ads inside gameplay.
        //  See README.md > "COPPA & EU / UK kids-app compliance" for the full checklist.

        setContent {
            AlphabetMatchTheme {
                AlphabetMatchApp(viewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppBackground()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppForeground()
    }
}

/**
 * State-driven navigation (deliberately simple: no nav graph, no deep links).
 */
@Composable
fun AlphabetMatchApp(viewModel: AlphabetViewModel) {
    val state by viewModel.uiState.collectAsState()

    // System back: Game asks for parental-gate confirmation; others go home.
    BackHandler(enabled = state.screen != AppScreen.Home) {
        when (state.screen) {
            AppScreen.Game -> viewModel.requestExitGame()
            else -> viewModel.goHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF6FF))
            .safeDrawingPadding(),
    ) {
        when (state.screen) {
            AppScreen.Home -> HomeScreen(
                stars = state.stars,
                onPlay = viewModel::startGame,
                onOpenSettings = viewModel::openSettings,
            )
            AppScreen.Game -> GameScreen(
                state = state,
                onConnect = viewModel::attemptConnect,
                onBackRequested = viewModel::requestExitGame,
                onConfirmExit = viewModel::goHome,
                onDismissExit = viewModel::dismissExitDialog,
            )
            AppScreen.Reward -> RewardScreen(
                stars = state.stars,
                stickerLetters = state.stickerLetters,
                onContinue = viewModel::continueFromReward,
                onHome = viewModel::goHome,
            )
            AppScreen.Settings -> SettingsScreen(
                soundEnabled = state.soundEnabled,
                musicEnabled = state.musicEnabled,
                onBack = viewModel::goHome,
                onToggleSound = viewModel::toggleSound,
                onToggleMusic = viewModel::toggleMusic,
                onResetProgress = viewModel::resetProgress,
            )
        }
    }
}
