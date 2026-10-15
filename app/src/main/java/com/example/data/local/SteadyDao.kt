package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ErpSession
import com.example.data.model.ImaginalScript
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import com.example.data.model.ReminderSchedule
import com.example.data.model.RitualLog
import com.example.data.model.SettingsEntity
import com.example.data.model.StruggleEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface SteadyDao {
    // Settings
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsEntity)

    // Ladder Items - sorted by baseline SUDS descending (hardest on top) or ascending
    @Query("SELECT * FROM ladder_items WHERE isArchived = 0 ORDER BY baselineSuds DESC")
    fun getLadderItemsBySudsDesc(): Flow<List<LadderItem>>

    @Query("SELECT * FROM ladder_items WHERE isArchived = 0 ORDER BY baselineSuds ASC")
    fun getLadderItemsBySudsAsc(): Flow<List<LadderItem>>

    @Query("SELECT * FROM ladder_items WHERE isArchived = 1 ORDER BY createdAt DESC")
    fun getArchivedLadderItems(): Flow<List<LadderItem>>

    @Query("SELECT * FROM ladder_items WHERE id = :id LIMIT 1")
    suspend fun getLadderItemById(id: Long): LadderItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLadderItem(item: LadderItem): Long

    @Update
    suspend fun updateLadderItem(item: LadderItem)

    @Delete
    suspend fun deleteLadderItem(item: LadderItem)

    // ERP Sessions
    @Query("SELECT * FROM erp_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ErpSession>>

    @Query("SELECT * FROM erp_sessions WHERE ladderItemId = :ladderItemId ORDER BY timestamp DESC")
    fun getSessionsForLadderItem(ladderItemId: Long): Flow<List<ErpSession>>

    @Query("SELECT * FROM erp_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): ErpSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ErpSession): Long

    @Query("SELECT COUNT(*) FROM erp_sessions")
    fun getSessionCountFlow(): Flow<Int>

    // Imaginal Scripts
    @Query("SELECT * FROM imaginal_scripts ORDER BY createdAt DESC")
    fun getAllImaginalScripts(): Flow<List<ImaginalScript>>

    @Query("SELECT * FROM imaginal_scripts WHERE id = :id LIMIT 1")
    suspend fun getImaginalScriptById(id: Long): ImaginalScript?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImaginalScript(script: ImaginalScript): Long

    @Delete
    suspend fun deleteImaginalScript(script: ImaginalScript)

    // Ritual Logs
    @Query("SELECT * FROM ritual_logs ORDER BY timestamp DESC")
    fun getAllRitualLogs(): Flow<List<RitualLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRitualLog(log: RitualLog): Long

    // Struggle Events
    @Query("SELECT * FROM struggle_events ORDER BY timestamp DESC")
    fun getAllStruggleEvents(): Flow<List<StruggleEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStruggleEvent(event: StruggleEvent): Long

    // Personal Goals
    @Query("SELECT * FROM personal_goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<PersonalGoal>>

    @Query("SELECT * FROM personal_goals WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveGoals(): Flow<List<PersonalGoal>>

    @Query("SELECT * FROM personal_goals WHERE id = :id LIMIT 1")
    suspend fun getGoalById(id: Long): PersonalGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: PersonalGoal): Long

    @Update
    suspend fun updateGoal(goal: PersonalGoal)

    @Delete
    suspend fun deleteGoal(goal: PersonalGoal)

    // Reminder Schedule
    @Query("SELECT * FROM reminder_schedules WHERE id = 1 LIMIT 1")
    fun getReminderScheduleFlow(): Flow<ReminderSchedule?>

    @Query("SELECT * FROM reminder_schedules WHERE id = 1 LIMIT 1")
    suspend fun getReminderSchedule(): ReminderSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminderSchedule(schedule: ReminderSchedule)

    // Reset data
    @Query("DELETE FROM ladder_items")
    suspend fun clearLadderItems()

    @Query("DELETE FROM erp_sessions")
    suspend fun clearSessions()

    @Query("DELETE FROM ritual_logs")
    suspend fun clearRitualLogs()

    @Query("DELETE FROM struggle_events")
    suspend fun clearStruggleEvents()

    @Query("DELETE FROM imaginal_scripts")
    suspend fun clearImaginalScripts()

    @Query("DELETE FROM personal_goals")
    suspend fun clearGoals()
}
