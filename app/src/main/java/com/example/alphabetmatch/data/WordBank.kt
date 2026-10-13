package com.example.alphabetmatch.data

import com.example.alphabetmatch.R

/**
 * A single picture the child can match: a word plus its drawable.
 *
 * @param letter    The first letter this word starts with (matching key, e.g. 'A').
 * @param label     Human-readable word shown on the placeholder card (e.g. "Apple").
 * @param imageRes  Drawable resource (placeholder `ph_*` box → real art later).
 */
data class Word(
    val letter: Char,
    val label: String,
    val imageRes: Int,
)

/**
 * The full A–Z word bank: 77 words across 26 letters (X has 2, the rest have 3).
 *
 * Single source of truth for randomly generated rounds. A round picks 3 letters and
 * one word per letter.
 *
 * TODO(ASSETS): every [Word.imageRes] points at a placeholder colored box
 *  (`res/drawable/ph_*.xml`). Drop in real PNG/WebP art with the same base name and
 *  PictureCard's Image() picks it up automatically – no code change needed.
 */
object WordBank {

    val byLetter: Map<Char, List<Word>> = mapOf(
        'A' to listOf(Word('A', "Apple", R.drawable.ph_apple), Word('A', "Airplane", R.drawable.ph_airplane), Word('A', "Ant", R.drawable.ph_ant)),
        'B' to listOf(Word('B', "Ball", R.drawable.ph_ball), Word('B', "Banana", R.drawable.ph_banana), Word('B', "Bear", R.drawable.ph_bear)),
        'C' to listOf(Word('C', "Cat", R.drawable.ph_cat), Word('C', "Car", R.drawable.ph_car), Word('C', "Cake", R.drawable.ph_cake)),
        'D' to listOf(Word('D', "Dog", R.drawable.ph_dog), Word('D', "Duck", R.drawable.ph_duck), Word('D', "Donkey", R.drawable.ph_donkey)),
        'E' to listOf(Word('E', "Egg", R.drawable.ph_egg), Word('E', "Elephant", R.drawable.ph_elephant), Word('E', "Eagle", R.drawable.ph_eagle)),
        'F' to listOf(Word('F', "Fish", R.drawable.ph_fish), Word('F', "Frog", R.drawable.ph_frog), Word('F', "Fridge", R.drawable.ph_fridge)),
        'G' to listOf(Word('G', "Grapes", R.drawable.ph_grapes), Word('G', "Gate", R.drawable.ph_gate), Word('G', "Guitar", R.drawable.ph_guitar)),
        'H' to listOf(Word('H', "Hat", R.drawable.ph_hat), Word('H', "House", R.drawable.ph_house), Word('H', "Horse", R.drawable.ph_horse)),
        'I' to listOf(Word('I', "Iron", R.drawable.ph_iron), Word('I', "Igloo", R.drawable.ph_igloo), Word('I', "Ice cream", R.drawable.ph_ice_cream)),
        'J' to listOf(Word('J', "Juice", R.drawable.ph_juice), Word('J', "Jam", R.drawable.ph_jam), Word('J', "Jellyfish", R.drawable.ph_jellyfish)),
        'K' to listOf(Word('K', "Kite", R.drawable.ph_kite), Word('K', "Koala", R.drawable.ph_koala), Word('K', "Key", R.drawable.ph_key)),
        'L' to listOf(Word('L', "Lion", R.drawable.ph_lion), Word('L', "Lemon", R.drawable.ph_lemon), Word('L', "Ladybug", R.drawable.ph_ladybug)),
        'M' to listOf(Word('M', "Monkey", R.drawable.ph_monkey), Word('M', "Moon", R.drawable.ph_moon), Word('M', "Milk", R.drawable.ph_milk)),
        'N' to listOf(Word('N', "Nest", R.drawable.ph_nest), Word('N', "Nose", R.drawable.ph_nose), Word('N', "Nut", R.drawable.ph_nut)),
        'O' to listOf(Word('O', "Orange", R.drawable.ph_orange), Word('O', "Owl", R.drawable.ph_owl), Word('O', "Octopus", R.drawable.ph_octopus)),
        'P' to listOf(Word('P', "Pig", R.drawable.ph_pig), Word('P', "Pizza", R.drawable.ph_pizza), Word('P', "Penguin", R.drawable.ph_penguin)),
        'Q' to listOf(Word('Q', "Queen", R.drawable.ph_queen), Word('Q', "Question mark", R.drawable.ph_question_mark), Word('Q', "Quilt", R.drawable.ph_quilt)),
        'R' to listOf(Word('R', "Rabbit", R.drawable.ph_rabbit), Word('R', "Rainbow", R.drawable.ph_rainbow), Word('R', "Robot", R.drawable.ph_robot)),
        'S' to listOf(Word('S', "Sun", R.drawable.ph_sun), Word('S', "Star", R.drawable.ph_star), Word('S', "Snake", R.drawable.ph_snake)),
        'T' to listOf(Word('T', "Tree", R.drawable.ph_tree), Word('T', "Turtle", R.drawable.ph_turtle), Word('T', "Train", R.drawable.ph_train)),
        'U' to listOf(Word('U', "Umbrella", R.drawable.ph_umbrella), Word('U', "Unicorn", R.drawable.ph_unicorn), Word('U', "UFO", R.drawable.ph_ufo)),
        'V' to listOf(Word('V', "Van", R.drawable.ph_van), Word('V', "Violin", R.drawable.ph_violin), Word('V', "Volcano", R.drawable.ph_volcano)),
        'W' to listOf(Word('W', "Whale", R.drawable.ph_whale), Word('W', "Watch", R.drawable.ph_watch), Word('W', "Watermelon", R.drawable.ph_watermelon)),
        'X' to listOf(Word('X', "Xylophone", R.drawable.ph_xylophone), Word('X', "X-ray", R.drawable.ph_x_ray)),
        'Y' to listOf(Word('Y', "Yo-yo", R.drawable.ph_yo_yo), Word('Y', "Yogurt", R.drawable.ph_yogurt), Word('Y', "Yak", R.drawable.ph_yak)),
        'Z' to listOf(Word('Z', "Zebra", R.drawable.ph_zebra), Word('Z', "Zipper", R.drawable.ph_zipper), Word('Z', "Zoo", R.drawable.ph_zoo)),
    )

    /** All 26 letters, in alphabetical order. */
    val allLetters: List<Char> = byLetter.keys.sorted()

    /** Per-letter "correct" sound (sfx_letter_a … sfx_letter_z). */
    private val letterSounds: Map<Char, Int> = mapOf(
        'A' to R.raw.sfx_letter_a, 'B' to R.raw.sfx_letter_b, 'C' to R.raw.sfx_letter_c,
        'D' to R.raw.sfx_letter_d, 'E' to R.raw.sfx_letter_e, 'F' to R.raw.sfx_letter_f,
        'G' to R.raw.sfx_letter_g, 'H' to R.raw.sfx_letter_h, 'I' to R.raw.sfx_letter_i,
        'J' to R.raw.sfx_letter_j, 'K' to R.raw.sfx_letter_k, 'L' to R.raw.sfx_letter_l,
        'M' to R.raw.sfx_letter_m, 'N' to R.raw.sfx_letter_n, 'O' to R.raw.sfx_letter_o,
        'P' to R.raw.sfx_letter_p, 'Q' to R.raw.sfx_letter_q, 'R' to R.raw.sfx_letter_r,
        'S' to R.raw.sfx_letter_s, 'T' to R.raw.sfx_letter_t, 'U' to R.raw.sfx_letter_u,
        'V' to R.raw.sfx_letter_v, 'W' to R.raw.sfx_letter_w, 'X' to R.raw.sfx_letter_x,
        'Y' to R.raw.sfx_letter_y, 'Z' to R.raw.sfx_letter_z,
    )

    /** All picture-words, flattened (used by tests / validation). */
    val allWords: List<Word> = byLetter.values.flatten()

    fun wordsFor(letter: Char): List<Word> = byLetter[letter].orEmpty()

    fun letterSound(letter: Char): Int = letterSounds[letter] ?: R.raw.sfx_letter_a
}

