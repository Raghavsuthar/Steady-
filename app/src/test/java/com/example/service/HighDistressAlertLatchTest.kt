package com.example.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HighDistressAlertLatchTest {
    @Test
    fun promptsOnlyOnceWhileTheSameHighDistressEpisodeContinues() {
        val latch = HighDistressAlertLatch()

        assertTrue(latch.shouldTrigger(currentSuds = 95, highSudsSeconds = 300))
        assertFalse(latch.shouldTrigger(currentSuds = 95, highSudsSeconds = 301))
        assertFalse(latch.shouldTrigger(currentSuds = 98, highSudsSeconds = 420))
    }

    @Test
    fun aNewEpisodeCanTriggerAfterDistressDropsBelowThreshold() {
        val latch = HighDistressAlertLatch()

        assertTrue(latch.shouldTrigger(currentSuds = 95, highSudsSeconds = 300))
        assertFalse(latch.shouldTrigger(currentSuds = 50, highSudsSeconds = 0))
        assertTrue(latch.shouldTrigger(currentSuds = 92, highSudsSeconds = 300))
    }

    @Test
    fun doesNotTriggerBeforeFiveMinutesAtHighDistress() {
        val latch = HighDistressAlertLatch()

        assertFalse(latch.shouldTrigger(currentSuds = 95, highSudsSeconds = 299))
        assertTrue(latch.shouldTrigger(currentSuds = 95, highSudsSeconds = 300))
    }
}
