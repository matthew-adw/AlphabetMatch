# Alphabet Match Kids (AlphabetMatchKids)

A toddler-friendly Jetpack Compose app: match letters to pictures that start with that letter. Built with **Kotlin DSL** Gradle files, Material 3, and an MVVM-lite structure.

- **No fail state, no timer, no score punishment** - wrong taps get a gentle shake and a soft "Try again!", correct taps get confetti + a happy sound + the next round.
- **Reward screen** after every 5 rounds (stars + letter stickers).
- **Settings** is a normal screen, reached via the gear icon on Home - no gates.

## Screens & structure

```text
app/src/main/java/com/example/alphabetmatch/
├── MainActivity.kt              # Hosts the app, state-driven navigation, back handling
├── audio/SoundManager.kt        # SFX + HOME/GAME music tracks
├── data/
│   ├── WordBank.kt              # 77-word A-Z picture bank (26 letters, 3 each)
│   ├── Round.kt                 # Random 3-letter <-> 3-picture round generator
│   └── ProgressRepository.kt    # DataStore Preferences: level, stars, toggles
└── ui/
    ├── AlphabetViewModel.kt     # Round, matched pairs, level number, 3-strike restart
    ├── components/              # Confetti, Shake, PictureCard, Buttons
    └── screens/                 # Home, Game (connect letters to pictures), Reward, Settings
```

## Game

The game shows **3 letters in a left column** and **3 pictures in a right column**; the child draws a line (or taps) from each letter to the picture that starts with that letter. A green line locks a match. After all 3 pairs are connected the child advances to the next round.

- **Level header** ("Level 1", "Level 2", ...) is the main on-screen focus.
- **3 wrong connections in a level** -> gently restart at level 1 (stars kept).
- **Reward screen** after every 5 stars, with star stickers.

## Building & running

Open in Android Studio and press **Run** (emulator/device, API 26+).

Command line (Windows):

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat assembleDebug
# APK: app\build\outputs\apk\debug\app-debug.apk
```

> Included helpers: `build_debug.cmd` (JAVA_HOME + gradle in one step) and
> `tools/gen_assets.ps1` which regenerates all placeholder art and silent audio.

## Replacing placeholder assets

Every image and sound today is a placeholder. Search for `TODO(ASSETS)` - these are the integration points:

| Location | What it marks |
|---|---|
| `data/WordBank.kt` | The 77 picture-words and per-letter sounds across the full A-Z |
| `ui/components/PictureCard.kt` | `Image(painterResource(imageRes))` - where real art is drawn |
| `res/drawable/ph_*.xml` | Colored placeholder box (TODO: replace with real artwork) |
| `res/raw/sfx_letter_*.wav` | Silent placeholder letter sounds |
| `res/raw/music_home.wav` / `music_game.wav` | Silent placeholder looping music tracks |

**Images** - export PNG/WebP (square, 512x512+). `tools/gen_assets.ps1` regenerates the `ph_*.xml` placeholders; delete the file and drop in `ph_<name>.png` with the same base name - no code changes needed. When real art is in place, set `showLabel = false` in `PictureCard.kt`.

**Sounds** - export **.ogg** (Vorbis, mono). Delete the `.wav` first, then add the `.ogg` with the same base name (two files can't share one resource name). Keep SFX <= 3 s; `music_bg` is looped by `SoundManager`.

## Tests

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat test
```

`WordBankTest` validates the 26 letters, the 3-variants-per-letter word bank (77 images), that every word starts with its own letter, and that generated rounds are valid.

## COPPA & EU / UK kids-app compliance

**The app today is compliant by design:**

- **No data leaves the device.** DataStore Preferences holds only a level, a star count and two sound toggles - no personal data, no identifiers, no profiling.
- **No third-party SDKs** (no analytics, no ads, no crash reporters).
- **No `INTERNET` permission** - the app cannot phone home.
- **No external links**, no browser, no sharing, no purchases.
- Play listing: answer "Appeals to children: yes", complete the Families/age questionnaire, and submit a Data safety form of **"No data collected"**.

**When you add AdMob later - keep these exact flags** (also saved as a comment in `MainActivity.onCreate`):

- **Child-directed flag: ON** -> `setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)`
- **Data tracking: DISABLED** -> `setTagForUnderAgeOfConsent(TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)`
- **Content rating: max "G"** -> `setMaxAdContentRating(MAX_AD_CONTENT_RATING_G)`

Also required by Google Play Families + COPPA + GDPR Art. 8 + UK Age Appropriate Design Code:

1. Request **non-personalized ads only** (no behavioural profiling).
2. Run AdMob **without the AD_ID permission** where possible for child-directed traffic.
3. Add the `INTERNET` permission only when the SDK is actually integrated.
4. Place ads **outside gameplay** (e.g. banner under the Home screen) - never interstitials during play, never shaped like game elements.
5. Re-check compliance before adding *any* other SDK; publish a privacy policy stating no collection, no sharing, child-directed ads only.
