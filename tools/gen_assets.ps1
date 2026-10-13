# Generates placeholder vector drawables + silent WAV audio for AlphabetMatchKids.
# Covers the full A-Z word bank (77 images), 26 letter sounds, and 2 music tracks.
$res = Join-Path $PSScriptRoot '..\app\src\main\res'
$drawable = Join-Path $res 'drawable'
$raw = Join-Path $res 'raw'
New-Item -ItemType Directory -Force -Path $drawable, $raw | Out-Null

# 77 placeholder words (A-Z; X has 2). Order matches the confirmed word bank.
$names = @(
    'ph_apple','ph_airplane','ph_ant',
    'ph_ball','ph_banana','ph_bear',
    'ph_cat','ph_car','ph_cake',
    'ph_dog','ph_duck','ph_donkey',
    'ph_egg','ph_elephant','ph_eagle',
    'ph_fish','ph_frog','ph_fridge',
    'ph_grapes','ph_gate','ph_guitar',
    'ph_hat','ph_house','ph_horse',
    'ph_iron','ph_igloo','ph_ice_cream',
    'ph_juice','ph_jam','ph_jellyfish',
    'ph_kite','ph_koala','ph_key',
    'ph_lion','ph_lemon','ph_ladybug',
    'ph_monkey','ph_moon','ph_milk',
    'ph_nest','ph_nose','ph_nut',
    'ph_orange','ph_owl','ph_octopus',
    'ph_pig','ph_pizza','ph_penguin',
    'ph_queen','ph_question_mark','ph_quilt',
    'ph_rabbit','ph_rainbow','ph_robot',
    'ph_sun','ph_star','ph_snake',
    'ph_tree','ph_turtle','ph_train',
    'ph_umbrella','ph_unicorn','ph_ufo',
    'ph_van','ph_violin','ph_volcano',
    'ph_whale','ph_watch','ph_watermelon',
    'ph_xylophone','ph_x_ray',
    'ph_yo_yo','ph_yogurt','ph_yak',
    'ph_zebra','ph_zipper','ph_zoo'
)

$palette = @(
    '#E53935','#1E88E5','#FB8C00','#43A047','#8E24AA',
    '#00ACC1','#FDD835','#EC407A','#6D4C41','#546E7A'
)

$pathData = 'M14,0 L86,0 A14,14 0 0 1 100,14 L100,86 A14,14 0 0 1 86,100 L14,100 A14,14 0 0 1 0,86 L0,14 A14,14 0 0 1 14,0 Z'
$nl = [Environment]::NewLine

for ($i = 0; $i -lt $names.Count; $i++) {
    $name = $names[$i]
    $color = $palette[$i % $palette.Count]
    $xml = '<vector xmlns:android="http://schemas.android.com/apk/res/android"' + $nl +
        '    android:width="120dp"' + $nl +
        '    android:height="120dp"' + $nl +
        '    android:viewportWidth="100"' + $nl +
        '    android:viewportHeight="100">' + $nl +
        '    <!-- TODO(ASSETS): placeholder colored box. Replace this file with real artwork for ' + $name + '. -->' + $nl +
        '    <path' + $nl +
        '        android:fillColor="' + $color + '" ' + $nl +
        '        android:pathData="' + $pathData + '" />' + $nl +
        '</vector>'
    [System.IO.File]::WriteAllText((Join-Path $drawable ($name + '.xml')), $xml)
}

# --- Silent placeholder WAVs (8 kHz mono 16-bit) ---
function New-SilentWav([string]$path, [double]$seconds) {
    $sampleRate = 8000
    $dataLen = [int]($sampleRate * 2 * $seconds)
    $ms = New-Object System.IO.MemoryStream
    $bw = New-Object System.IO.BinaryWriter($ms)
    $enc = [System.Text.Encoding]::ASCII
    $bw.Write($enc.GetBytes('RIFF'))
    $bw.Write([int](36 + $dataLen))
    $bw.Write($enc.GetBytes('WAVE'))
    $bw.Write($enc.GetBytes('fmt '))
    $bw.Write([int]16)
    $bw.Write([System.Int16]1)   # PCM
    $bw.Write([System.Int16]1)   # mono
    $bw.Write([int]8000)  # sample rate
    $bw.Write([int]16000) # byte rate
    $bw.Write([System.Int16]2)   # block align
    $bw.Write([System.Int16]16)  # bits per sample
    $bw.Write($enc.GetBytes('data'))
    $bw.Write([int]$dataLen)
    $bw.Write((New-Object byte[] $dataLen))
    [System.IO.File]::WriteAllBytes($path, $ms.ToArray())
    $bw.Close()
}

# 26 letter sounds a-z + feedback SFX.
$letters = 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
foreach ($l in $letters) {
    New-SilentWav (Join-Path $raw ("sfx_letter_$l" + '.wav')) 0.5
}
New-SilentWav (Join-Path $raw 'sfx_correct.wav') 0.5
New-SilentWav (Join-Path $raw 'sfx_try_again.wav') 0.5

# Two looping background music tracks (home screen + gameplay).
New-SilentWav (Join-Path $raw 'music_home.wav') 2.0
New-SilentWav (Join-Path $raw 'music_game.wav') 2.0

# Remove the old single track if present (superseded by home/game).
$oldMusic = Join-Path $raw 'music_bg.wav'
if (Test-Path $oldMusic) { Remove-Item $oldMusic -Force }

Write-Host ('Drawables: ' + $names.Count)
Write-Host 'Letter sounds: 26 + sfx_correct + sfx_try_again'
Write-Host 'Music: music_home.wav, music_game.wav'
