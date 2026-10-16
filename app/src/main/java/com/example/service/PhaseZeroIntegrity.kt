package com.example.service

import java.util.Calendar
import java.util.TimeZone

/**
 * Latches the high-distress safety prompt for one sustained episode.
 * A new episode begins when distress falls below the configured threshold.
 */
internal class HighDistressAlertLatch(
    private val thresholdSuds: Int = 90,
    private val thresholdSeconds: Int = 300
) {
    private var triggeredForEpisode = false

    fun shouldTrigger(currentSuds: Int, highSudsSeconds: Int): Boolean {
        if (currentSuds < thresholdSuds) {
            triggeredForEpisode = false
            return false
        }
        if (highSudsSeconds >= thresholdSeconds && !triggeredForEpisode) {
            triggeredForEpisode = true
            return true
        }
        return false
    }

    fun reset() {
        triggeredForEpisode = false
    }
}

/** Count distinct local calendar days in the current month; never invent empty-history progress. */
internal fun countDistinctPracticeDaysInCurrentMonth(
    timestamps: List<Long>,
    nowMillis: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getDefault()
): Int {
    val now = Calendar.getInstance(timeZone).apply { timeInMillis = nowMillis }
    val currentYear = now.get(Calendar.YEAR)
    val currentMonth = now.get(Calendar.MONTH)

    return timestamps.asSequence()
        .filter { timestamp ->
            val date = Calendar.getInstance(timeZone).apply { timeInMillis = timestamp }
            date.get(Calendar.YEAR) == currentYear &&
                date.get(Calendar.MONTH) == currentMonth
        }
        .map { timestamp ->
            Calendar.getInstance(timeZone).apply { timeInMillis = timestamp }.let { date ->
                Triple(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
            }
        }
        .distinct()
        .count()
}

/** An ERP session is complete only when the exposure timer actually reaches zero. */
internal fun isErpSessionCompleted(remainingSeconds: Int): Boolean = remainingSeconds <= 0
