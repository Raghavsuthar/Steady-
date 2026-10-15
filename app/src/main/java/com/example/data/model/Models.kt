package com.example.data.model

import androidx.room.Entity
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
    val demoDataLoaded: Boolean = false
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
    val isArchived: Boolean = false
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
    val wasCompleted: Boolean = true
)

@Entity(tableName = "imaginal_scripts")
data class ImaginalScript(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val text: String,
    val audioPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ritual_logs")
data class RitualLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val trigger: String = "",
    val durationMinutes: Int = 10,
    val outcome: String = "RESISTED", // RESISTED, DELAYED, PERFORMED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "struggle_events")
data class StruggleEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val struggleType: String = "RITUAL_URGE", // RITUAL_URGE, INTRUSIVE_THOUGHT, NOT_SURE
    val startSuds: Int = 80,
    val endSuds: Int = 40,
    val delayMinutes: Int = 10,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
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
    val lastNotificationTimestamp: Long = 0L
)

// Helper structure for parsed check-ins
data class SudsCheckIn(
    val minuteOffset: Int,
    val suds: Int
)
