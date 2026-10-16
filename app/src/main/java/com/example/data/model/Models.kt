package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val hasCompletedOnboarding: Boolean = false,
    val userName: String = "",
    val ocdThemes: String = "",
    val reminderTime: String = "20:00",
    val isDarkTheme: Boolean? = null,
    val reducedMotion: Boolean = false,
    val largeText: Boolean = false,
    val aiCoachEnabled: Boolean = false,
    val aiCoachConsentAccepted: Boolean = false,
    val language: String = "en",
    val allowNotifications: Boolean = false,
    val demoDataLoaded: Boolean = false,
    @ColumnInfo(defaultValue = "0") val appLockEnabled: Boolean = false,
    @ColumnInfo(defaultValue = "5") val appLockTimeoutMinutes: Int = 5,
    @ColumnInfo(defaultValue = "0") val secureScreenEnabled: Boolean = false,
    @ColumnInfo(defaultValue = "0") val discreetMode: Boolean = false,
    @ColumnInfo(defaultValue = "0") val notificationsEnabled: Boolean = false,
    @ColumnInfo(defaultValue = "''") val reminderTimes: String = "",
    @ColumnInfo(defaultValue = "''") val quietHoursStart: String = "",
    @ColumnInfo(defaultValue = "''") val quietHoursEnd: String = "",
    @ColumnInfo(defaultValue = "'IN'") val crisisRegion: String = "IN",
    @ColumnInfo(defaultValue = "'SYSTEM'") val themeMode: String = "SYSTEM",
    @ColumnInfo(defaultValue = "''") val myWhy: String = "",
    @ColumnInfo(defaultValue = "0") val sampleDataActive: Boolean = false
)

@Entity(tableName = "ladder_items")
data class LadderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Contamination, Checking, Harm, Symmetry / Order, Unwanted Thoughts, Taboo Themes, Other
    val baselineSuds: Int, // 0 - 100
    val status: String = "NOT_STARTED", // NOT_STARTED, IN_PROGRESS, MASTERED
    val notes: String = "",
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val sessionCount: Int = 0,
    val lastSuds: Int? = null,
    val personalGoalId: Long? = null,
    val isArchived: Boolean = false,
    @ColumnInfo(defaultValue = "''") val responsePreventionRule: String = "",
    @ColumnInfo(defaultValue = "'[]'") val safetyBehaviorsToDrop: String = "[]",
    @ColumnInfo(defaultValue = "''") val avoidanceNotes: String = "",
    @ColumnInfo(defaultValue = "'OTHER'") val themeTag: String = "OTHER",
    @ColumnInfo(defaultValue = "0") val clinicianApproved: Boolean = false,
    @ColumnInfo(defaultValue = "0") val isSample: Boolean = false,
    @ColumnInfo(defaultValue = "NULL") val archivedAt: Long? = null
)

@Entity(tableName = "erp_sessions")
data class ErpSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ladderItemId: Long? = null,
    val ladderItemTitle: String = "",
    val sessionType: String = "IN_VIVO", // IN_VIVO or IMAGINAL
    val predictionText: String = "",
    val predictionBeliefStart: Int = 80, // 0 - 100%
    val startSuds: Int = 70, // 0 - 100
    val checkInsJson: String = "", // e.g. "0:75,180:70,360:55,540:40,720:30"
    val endSuds: Int = 30,
    val predictionBeliefEnd: Int = 25,
    val learnedNote: String = "",
    val durationMinutes: Int = 15,
    val timestamp: Long = System.currentTimeMillis(),
    val compulsionResisted: String = "",
    val alternativeAction: String = "",
    val wasCompleted: Boolean = true,
    @ColumnInfo(defaultValue = "NULL") val willingnessStart: Int? = null,
    @ColumnInfo(defaultValue = "NULL") val willingnessEnd: Int? = null,
    @ColumnInfo(defaultValue = "NULL") val tolerabilityBeliefStart: Int? = null,
    @ColumnInfo(defaultValue = "NULL") val tolerabilityBeliefEnd: Int? = null,
    @ColumnInfo(defaultValue = "NULL") val ritualFree: Boolean? = null,
    @ColumnInfo(defaultValue = "'[]'") val safetyBehaviorsUsed: String = "[]",
    @ColumnInfo(defaultValue = "'[]'") val contextTags: String = "[]",
    @ColumnInfo(defaultValue = "NULL") val plannedSessionId: Long? = null,
    @ColumnInfo(defaultValue = "0") val uncertaintyTarget: Int = 0,
    @ColumnInfo(defaultValue = "0") val isSample: Boolean = false
)

@Entity(tableName = "imaginal_scripts")
data class ImaginalScript(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val text: String,
    val audioPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "NULL") val audioFileName: String? = null,
    @ColumnInfo(defaultValue = "NULL") val durationSeconds: Int? = null,
    @ColumnInfo(defaultValue = "'OTHER'") val themeTag: String = "OTHER",
    @ColumnInfo(defaultValue = "0") val clinicianApproved: Boolean = false
)

@Entity(tableName = "ritual_logs")
data class RitualLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val trigger: String = "",
    val durationMinutes: Int = 10,
    val outcome: String = "RESISTED", // RESISTED, DELAYED, PERFORMED
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "'[\"OTHER\"]'") val categories: String = "[\"OTHER\"]",
    @ColumnInfo(defaultValue = "0") val isMental: Boolean = false,
    @ColumnInfo(defaultValue = "0") val askedOthersTimes: Int = 0,
    @ColumnInfo(defaultValue = "0") val isSample: Boolean = false
)

@Entity(tableName = "struggle_events")
data class StruggleEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val struggleType: String = "RITUAL_URGE", // RITUAL_URGE, INTRUSIVE_THOUGHT, NOT_SURE
    val startSuds: Int = 80,
    val endSuds: Int = 40,
    val delayMinutes: Int = 10,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0") val didDelay: Boolean = false,
    @ColumnInfo(defaultValue = "'STILL_DECIDING'") val finalChoice: String = "STILL_DECIDING",
    @ColumnInfo(defaultValue = "'[]'") val categories: String = "[]",
    @ColumnInfo(defaultValue = "0") val isSample: Boolean = false
)

@Entity(tableName = "personal_goals")
data class PersonalGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Daily Living", // Daily Living, Relationships, Work / Study, Self-Care, Recreation
    val isActive: Boolean = true,
    val daysPracticedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPracticedDate: Long? = null
)

@Entity(tableName = "reminder_schedules")
data class ReminderSchedule(
    @PrimaryKey val id: Int = 1,
    val isEnabled: Boolean = false,
    val timeHour: Int = 20,
    val timeMinute: Int = 0,
    val weekdaysOnly: Boolean = false,
    val includeWeekends: Boolean = true,
    val lastNotificationTimestamp: Long = 0L,
    @ColumnInfo(defaultValue = "127") val daysOfWeekMask: Int = 127,
    @ColumnInfo(defaultValue = "NULL") val lastScheduledAt: Long? = null
)

@Entity(
    tableName = "goal_practice_log",
    primaryKeys = ["goalId", "localDate"],
    foreignKeys = [
        ForeignKey(
            entity = PersonalGoal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["localDate"])]
)
data class GoalPracticeLog(
    val goalId: Long,
    val localDate: String,
    val createdAt: Long
)

@Entity(
    tableName = "imaginal_sessions",
    foreignKeys = [
        ForeignKey(
            entity = ImaginalScript::class,
            parentColumns = ["id"],
            childColumns = ["scriptId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["startedAt"])]
)
data class ImaginalSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(defaultValue = "NULL") val scriptId: Long? = null,
    val startedAt: Long,
    @ColumnInfo(defaultValue = "0") val durationSeconds: Int = 0,
    @ColumnInfo(defaultValue = "1") val loops: Int = 1,
    @ColumnInfo(defaultValue = "NULL") val startSuds: Int? = null,
    @ColumnInfo(defaultValue = "NULL") val endSuds: Int? = null,
    @ColumnInfo(defaultValue = "NULL") val beliefStart: Int? = null,
    @ColumnInfo(defaultValue = "NULL") val beliefEnd: Int? = null,
    @ColumnInfo(defaultValue = "''") val note: String = "",
    val createdAt: Long
)

@Entity(
    tableName = "planned_sessions",
    foreignKeys = [
        ForeignKey(
            entity = LadderItem::class,
            parentColumns = ["id"],
            childColumns = ["ladderItemId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["scheduledAt"]), Index(value = ["ladderItemId"])]
)
data class PlannedSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(defaultValue = "NULL") val ladderItemId: Long? = null,
    val scheduledAt: Long,
    @ColumnInfo(defaultValue = "'PLANNED'") val status: String = "PLANNED",
    @ColumnInfo(defaultValue = "NULL") val reminderLeadMinutes: Int? = null,
    @ColumnInfo(defaultValue = "''") val ifThenPlan: String = "",
    val createdAt: Long
)

@Entity(tableName = "checkins", indices = [Index(value = ["date"])])
data class Checkin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val instrumentId: String,
    @ColumnInfo(defaultValue = "'{}'") val answersJson: String = "{}",
    @ColumnInfo(defaultValue = "NULL") val totalScore: Int? = null,
    @ColumnInfo(defaultValue = "''") val note: String = "",
    val createdAt: Long
)

@Entity(tableName = "active_session_state")
data class ActiveSessionState(
    @PrimaryKey @ColumnInfo(defaultValue = "1") val id: Int = 1,
    val sessionType: String,
    val stateJson: String,
    val updatedAt: Long
)

// Helper structure for parsed check-ins
data class SudsCheckIn(
    val minuteOffset: Int,
    val suds: Int
)
