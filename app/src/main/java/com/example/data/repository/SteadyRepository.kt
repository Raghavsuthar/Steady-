package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.local.SteadyDatabase
import com.example.data.local.SteadyDao
import com.example.data.local.populateSamplePracticeData
import com.example.data.model.ErpSession
import com.example.data.model.GoalPracticeLog
import com.example.data.model.ImaginalScript
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import com.example.data.model.ReminderSchedule
import com.example.data.model.RitualLog
import com.example.data.model.SettingsEntity
import com.example.data.model.StruggleEvent
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Locale

class SteadyRepository(private val database: SteadyDatabase) {
    private val dao: SteadyDao = database.steadyDao()
    val settingsFlow: Flow<SettingsEntity?> = dao.getSettingsFlow()
    val ladderItemsDesc: Flow<List<LadderItem>> = dao.getLadderItemsBySudsDesc()
    val ladderItemsAsc: Flow<List<LadderItem>> = dao.getLadderItemsBySudsAsc()
    val archivedLadderItemsFlow: Flow<List<LadderItem>> = dao.getArchivedLadderItems()
    val sessionsFlow: Flow<List<ErpSession>> = dao.getAllSessions()
    val ritualLogsFlow: Flow<List<RitualLog>> = dao.getAllRitualLogs()
    val struggleEventsFlow: Flow<List<StruggleEvent>> = dao.getAllStruggleEvents()
    val imaginalScriptsFlow: Flow<List<ImaginalScript>> = dao.getAllImaginalScripts()
    val allGoalsFlow: Flow<List<PersonalGoal>> = dao.getAllGoals()
    val activeGoalsFlow: Flow<List<PersonalGoal>> = dao.getActiveGoals()
    val reminderScheduleFlow: Flow<ReminderSchedule?> = dao.getReminderScheduleFlow()

    suspend fun getSettings(): SettingsEntity? = dao.getSettings()

    suspend fun updateSettings(settings: SettingsEntity) {
        dao.saveSettings(settings)
    }

    suspend fun addLadderItem(item: LadderItem): Long = dao.insertLadderItem(item)

    suspend fun updateLadderItem(item: LadderItem) = dao.updateLadderItem(item)

    suspend fun archiveLadderItem(item: LadderItem, archive: Boolean) {
        dao.updateLadderItem(item.copy(isArchived = archive))
    }

    suspend fun deleteLadderItem(item: LadderItem) = dao.deleteLadderItem(item)

    suspend fun recordSession(session: ErpSession) {
        database.withTransaction {
            dao.insertSession(session)
            session.ladderItemId?.let { ladderId ->
                val existing = dao.getLadderItemById(ladderId)
                if (existing != null) {
                    val newCount = existing.sessionCount + 1
                    // Evidence-informed progression: requires at least 3 repeat sessions with end SUDS <= 40% baseline
                    val isMastered = (newCount >= 3 && session.endSuds <= (existing.baselineSuds * 0.4))
                    val newStatus = if (existing.status == "MASTERED" || isMastered) "MASTERED" else "IN_PROGRESS"
                    dao.updateLadderItem(
                        existing.copy(
                            sessionCount = newCount,
                            lastSuds = session.endSuds,
                            status = newStatus
                        )
                    )

                    // Goal practice history and progress are part of this session transaction.
                    existing.personalGoalId?.let { goalId ->
                        incrementGoalPracticeInternal(goalId, session.timestamp)
                    }
                }
            }
        }
    }

    suspend fun addGoal(goal: PersonalGoal): Long = dao.insertGoal(goal)

    suspend fun updateGoal(goal: PersonalGoal) = dao.updateGoal(goal)

    suspend fun deleteGoal(goal: PersonalGoal) = dao.deleteGoal(goal)

    suspend fun incrementGoalPractice(goalId: Long, timestamp: Long = System.currentTimeMillis()) {
        database.withTransaction {
            incrementGoalPracticeInternal(goalId, timestamp)
        }
    }

    private suspend fun incrementGoalPracticeInternal(goalId: Long, timestamp: Long) {
        val goal = dao.getGoalById(goalId) ?: return
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val localDate = String.format(
            Locale.US,
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // The composite primary key makes repeated practice on one day idempotent.
        dao.insertGoalPracticeLog(
            GoalPracticeLog(goalId = goalId, localDate = localDate, createdAt = timestamp)
        )
        // Persisted unique rows, not an in-memory increment, are the source of truth.
        dao.updateGoal(
            goal.copy(
                daysPracticedCount = dao.getGoalPracticeDayCount(goalId),
                lastPracticedDate = timestamp
            )
        )
    }

    suspend fun updateReminderSchedule(schedule: ReminderSchedule) {
        dao.saveReminderSchedule(schedule)
    }

    suspend fun addRitualLog(log: RitualLog): Long = dao.insertRitualLog(log)

    suspend fun addStruggleEvent(event: StruggleEvent): Long = dao.insertStruggleEvent(event)

    suspend fun addImaginalScript(script: ImaginalScript): Long = dao.insertImaginalScript(script)

    suspend fun deleteImaginalScript(script: ImaginalScript) = dao.deleteImaginalScript(script)

    suspend fun loadSampleData() {
        populateSamplePracticeData(dao)
    }

    suspend fun clearAllData() {
        dao.clearLadderItems()
        dao.clearSessions()
        dao.clearRitualLogs()
        dao.clearStruggleEvents()
        dao.clearImaginalScripts()
        dao.clearGoals()
        // Reset settings demo status
        val current = dao.getSettings() ?: SettingsEntity()
        dao.saveSettings(current.copy(demoDataLoaded = false))
    }
}
