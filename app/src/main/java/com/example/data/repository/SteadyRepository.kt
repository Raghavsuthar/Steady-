package com.example.data.repository

import com.example.data.local.SteadyDao
import com.example.data.local.populateSamplePracticeData
import com.example.data.model.ErpSession
import com.example.data.model.ImaginalScript
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import com.example.data.model.ReminderSchedule
import com.example.data.model.RitualLog
import com.example.data.model.SettingsEntity
import com.example.data.model.StruggleEvent
import kotlinx.coroutines.flow.Flow

class SteadyRepository(private val dao: SteadyDao) {
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

                // If linked to personal goal, increment goal practice count
                existing.personalGoalId?.let { goalId ->
                    incrementGoalPractice(goalId)
                }
            }
        }
    }

    suspend fun addGoal(goal: PersonalGoal): Long = dao.insertGoal(goal)

    suspend fun updateGoal(goal: PersonalGoal) = dao.updateGoal(goal)

    suspend fun deleteGoal(goal: PersonalGoal) = dao.deleteGoal(goal)

    suspend fun incrementGoalPractice(goalId: Long) {
        val goal = dao.getGoalById(goalId)
        if (goal != null) {
            dao.updateGoal(
                goal.copy(
                    daysPracticedCount = goal.daysPracticedCount + 1,
                    lastPracticedDate = System.currentTimeMillis()
                )
            )
        }
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
