package com.example.alphabetmatch

import com.example.alphabetmatch.data.RoundGenerator
import com.example.alphabetmatch.data.WordBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordBankTest {

    @Test
    fun `word bank covers all 26 letters`() {
        assertEquals(('A'..'Z').toList(), WordBank.allLetters)
    }

    @Test
    fun `every letter has at least 2 words`() {
        // Flexible count: words can be added to any letter freely (X currently has 2,
        // most have 3) - only the floor is enforced.
        WordBank.byLetter.forEach { (letter, words) ->
            assertTrue("letter $letter needs at least 2 words", words.size >= 2)
        }
        // Sanity check: bank is substantial enough for varied rounds.
        assertTrue("word bank too small", WordBank.allWords.size >= 77)
    }

    @Test
    fun `every word starts with its letter`() {
        WordBank.byLetter.forEach { (letter, words) ->
            words.forEach { word ->
                assertEquals(
                    "word '${word.label}' does not start with $letter",
                    letter,
                    word.label.first().uppercaseChar(),
                )
                assertEquals("word '${word.label}' letter field mismatch", letter, word.letter)
            }
        }
    }

    @Test
    fun `all image resource ids are present and labels non-blank`() {
        WordBank.allWords.forEach { word ->
            assertTrue("image missing for ${word.label}", word.imageRes != 0)
            assertTrue("label blank for res ${word.imageRes}", word.label.isNotBlank())
        }
    }

    @Test
    fun `all labels are unique`() {
        val labels = WordBank.allWords.map { it.label }
        assertEquals("duplicate labels found", labels.size, labels.toSet().size)
    }

    @Test
    fun `every letter has a sound resource`() {
        WordBank.allLetters.forEach { letter ->
            assertTrue("sound missing for $letter", WordBank.letterSound(letter) != 0)
        }
    }

    @Test
    fun `generated round has 3 distinct letters and matching words`() {
        repeat(50) {
            val round = RoundGenerator.generate()
            assertEquals(3, round.leftLetters.size)
            assertEquals(3, round.rightWords.size)
            assertEquals("left letters not distinct", 3, round.leftLetters.toSet().size)
            // Right column's letters are exactly the left column's letters.
            assertEquals(round.leftLetters.toSet(), round.rightWords.map { it.letter }.toSet())
            // Every left letter has exactly one correct right index.
            round.leftLetters.forEachIndexed { i, _ ->
                val correctCount = (0 until 3).count { j -> round.matches(i, j) }
                assertEquals("letter ${round.leftLetters[i]} must match exactly one picture", 1, correctCount)
            }
        }
    }

    @Test
    fun `rounds are randomly generated (not always identical)`() {
        // Extremely unlikely to produce the same round50 times in a row by chance.
        val rounds = (0 until 50).map { RoundGenerator.generate() }.map { it.leftLetters to it.rightWords.map { w -> w.label } }
        assertNotEquals(rounds.toSet().size, 1)
    }
}
