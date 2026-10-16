package com.example.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.example.data.local.SteadyDatabase
import com.example.data.model.ErpSession
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SteadyRepositoryTransactionTest {
    private lateinit var database: SteadyDatabase
    private lateinit var repository: SteadyRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            SteadyDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = SteadyRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun recordSessionPersistsSessionAndLinkedLadderAndGoalProgress() = runBlocking {
        val goalId = database.steadyDao().insertGoal(PersonalGoal(title = "Daily activity"))
        val ladderId = database.steadyDao().insertLadderItem(
            LadderItem(title = "Touch a door handle", category = "Contamination", baselineSuds = 50, personalGoalId = goalId)
        )
        val timestamp = localTimestamp(2026, Calendar.OCTOBER, 16)

        repository.recordSession(session(ladderId, timestamp, endSuds = 20))

        val stored = database.steadyDao().getSessionById(1L)
        assertNotNull(stored)
        val ladder = database.steadyDao().getLadderItemById(ladderId)!!
        assertEquals(1, ladder.sessionCount)
        assertEquals(20, ladder.lastSuds)
        assertEquals("IN_PROGRESS", ladder.status)
        assertEquals(1, database.steadyDao().getGoalPracticeDayCount(goalId))
        assertEquals(1, database.steadyDao().getGoalById(goalId)!!.daysPracticedCount)
    }

    @Test
    fun repeatedSessionsOnSameLocalDateCountAsOnePracticeDay() = runBlocking {
        val goalId = database.steadyDao().insertGoal(PersonalGoal(title = "Daily activity"))
        val ladderId = database.steadyDao().insertLadderItem(
            LadderItem(title = "Touch a door handle", category = "Contamination", baselineSuds = 50, personalGoalId = goalId)
        )
        val first = localTimestamp(2026, Calendar.OCTOBER, 16, 9)
        val second = localTimestamp(2026, Calendar.OCTOBER, 16, 18)

        repository.recordSession(session(ladderId, first))
        repository.recordSession(session(ladderId, second))

        assertEquals(2, database.steadyDao().getAllSessions().first().size)
        assertEquals(1, database.steadyDao().getGoalPracticeDayCount(goalId))
        assertEquals(1, database.steadyDao().getGoalById(goalId)!!.daysPracticedCount)
    }

    @Test
    fun sessionsOnDifferentLocalDatesCountAsDistinctPracticeDays() = runBlocking {
        val goalId = database.steadyDao().insertGoal(PersonalGoal(title = "Daily activity"))
        val ladderId = database.steadyDao().insertLadderItem(
            LadderItem(title = "Touch a door handle", category = "Contamination", baselineSuds = 50, personalGoalId = goalId)
        )

        repository.recordSession(session(ladderId, localTimestamp(2026, Calendar.OCTOBER, 16)))
        repository.recordSession(session(ladderId, localTimestamp(2026, Calendar.OCTOBER, 17)))

        assertEquals(2, database.steadyDao().getGoalPracticeDayCount(goalId))
        assertEquals(2, database.steadyDao().getGoalById(goalId)!!.daysPracticedCount)
    }

    @Test
    fun earlyEndedSessionRemainsIncompleteInStoredHistory() = runBlocking {
        val earlyEnded = ErpSession(
            ladderItemTitle = "Free Exposure",
            timestamp = localTimestamp(2026, Calendar.OCTOBER, 16),
            wasCompleted = false
        )

        repository.recordSession(earlyEnded)

        val stored = database.steadyDao().getSessionById(1L)
        assertNotNull(stored)
        assertFalse(stored!!.wasCompleted)
    }


    @Test
    fun failureDuringGoalPracticeWriteRollsBackSessionAndLadderProgress() = runBlocking {
        val goalId = database.steadyDao().insertGoal(PersonalGoal(title = "Daily activity"))
        val ladderId = database.steadyDao().insertLadderItem(
            LadderItem(title = "Touch a door handle", category = "Contamination", baselineSuds = 50, personalGoalId = goalId)
        )
        database.openHelper.writableDatabase.execSQL(
            """CREATE TRIGGER fail_goal_practice_insert
               BEFORE INSERT ON goal_practice_log
               BEGIN SELECT RAISE(ABORT, 'forced test failure'); END"""
        )

        var failure: Throwable? = null
        try {
            repository.recordSession(session(ladderId, localTimestamp(2026, Calendar.OCTOBER, 16)))
        } catch (caught: Throwable) {
            failure = caught
        }

        assertNotNull("The forced persistence failure must reach the caller", failure)
        assertEquals(0, database.steadyDao().getAllSessions().first().size)
        assertEquals(0, database.steadyDao().getLadderItemById(ladderId)!!.sessionCount)
        assertEquals(0, database.steadyDao().getGoalPracticeDayCount(goalId))
        assertEquals(0, database.steadyDao().getGoalById(goalId)!!.daysPracticedCount)
    }

    private fun session(ladderId: Long, timestamp: Long, endSuds: Int = 30) = ErpSession(
        ladderItemId = ladderId,
        ladderItemTitle = "Touch a door handle",
        startSuds = 50,
        endSuds = endSuds,
        timestamp = timestamp
    )

    private fun localTimestamp(year: Int, month: Int, day: Int, hour: Int = 12): Long =
        Calendar.getInstance(TimeZone.getDefault()).apply {
            clear()
            set(year, month, day, hour, 0, 0)
        }.timeInMillis
}
