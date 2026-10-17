package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BubblePopGameStateTest {
    @Test
    fun tappingCurrentBubbleIncreasesScoreByOne() {
        val state = BubblePopGameState()
        val width = 1000f
        val height = 800f
        val target = state.currentTarget(width, height)

        val next = state.tap(target.centerX, target.centerY, width, height)

        assertEquals(1, next.score)
    }

    @Test
    fun tappingOutsideCurrentBubbleDoesNotIncreaseScore() {
        val state = BubblePopGameState()
        val next = state.tap(1f, 1f, 1000f, 800f)

        assertEquals(0, next.score)
    }

    @Test
    fun roundCompletesAfterTenSuccessfulTaps() {
        var state = BubblePopGameState()
        repeat(BubblePopGameState.ROUND_LENGTH) {
            val target = state.currentTarget(1000f, 800f)
            state = state.tap(target.centerX, target.centerY, 1000f, 800f)
        }

        assertTrue(state.isComplete)
        assertEquals(BubblePopGameState.ROUND_LENGTH, state.score)
    }

    @Test
    fun restartReturnsToAnEmptyRound() {
        var state = BubblePopGameState()
        repeat(3) {
            val target = state.currentTarget(1000f, 800f)
            state = state.tap(target.centerX, target.centerY, 1000f, 800f)
        }

        val restarted = state.restart()

        assertEquals(0, restarted.score)
        assertFalse(restarted.isComplete)
    }
}
