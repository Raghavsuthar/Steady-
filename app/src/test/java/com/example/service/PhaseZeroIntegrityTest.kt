package com.example.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class PhaseZeroIntegrityTest {
    @Test
    fun practiceDaysCountDistinctLocalDatesWithinTheCurrentMonth() {
        val zone = TimeZone.getTimeZone("UTC")
        val now = calendar(zone, 2026, Calendar.OCTOBER, 16, 12).timeInMillis
        val sameDayMorning = calendar(zone, 2026, Calendar.OCTOBER, 15, 8).timeInMillis
        val sameDayEvening = calendar(zone, 2026, Calendar.OCTOBER, 15, 21).timeInMillis
        val anotherDay = calendar(zone, 2026, Calendar.OCTOBER, 2, 10).timeInMillis
        val priorMonth = calendar(zone, 2026, Calendar.SEPTEMBER, 30, 23).timeInMillis

        assertEquals(
            2,
            countDistinctPracticeDaysInCurrentMonth(
                listOf(sameDayMorning, sameDayEvening, anotherDay, priorMonth),
                now,
                zone
            )
        )
    }

    @Test
    fun emptyPracticeHistoryCountsAsZeroRatherThanInventingProgress() {
        assertEquals(0, countDistinctPracticeDaysInCurrentMonth(emptyList(), 1_797_000_000_000L))
    }

    @Test
    fun sessionIsCompleteOnlyWhenTheTimerReachedZero() {
        assertTrue(isErpSessionCompleted(0))
        assertTrue(isErpSessionCompleted(-1))
        assertFalse(isErpSessionCompleted(1))
        assertFalse(isErpSessionCompleted(600))
    }

    private fun calendar(
        zone: TimeZone,
        year: Int,
        month: Int,
        day: Int,
        hour: Int
    ): Calendar = Calendar.getInstance(zone).apply {
        clear()
        set(year, month, day, hour, 0, 0)
    }
}
