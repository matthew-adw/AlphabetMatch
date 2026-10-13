package com.example.alphabetmatch.data

/**
 * One randomly generated round: 3 letters on the left, 3 pictures on the right.
 *
 * A connection from left index [i] to right index [j] is correct when
 * `rightWords[j].letter == leftLetters[i]`. Both columns are shuffled independently,
 * so the matching pairs are never in the same row.
 *
 * @param leftLetters  Three distinct letters (order = left column, top → bottom).
 * @param rightWords   One word per letter (order = right column, top → bottom).
 */
data class Round(
    val leftLetters: List<Char>,
    val rightWords: List<Word>,
) {
    fun matches(leftIndex: Int, rightIndex: Int): Boolean =
        rightWords.getOrNull(rightIndex)?.letter == leftLetters.getOrNull(leftIndex)
}

/** Builds fresh, randomised rounds from the [WordBank]. */
object RoundGenerator {

    /** Number of pairs (letters/pictures) per round. */
    const val PAIRS = 3

    /**
     * Pick [PAIRS] distinct letters, choose one of each letter's words at random,
     * then shuffle the left and right columns independently.
     */
    fun generate(): Round {
        val letters = WordBank.allLetters.shuffled().take(PAIRS)
        val words = letters.map { WordBank.wordsFor(it).random() }
        return Round(leftLetters = letters.shuffled(), rightWords = words.shuffled())
    }
}
