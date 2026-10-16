package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ErpSession
import com.example.data.model.ImaginalScript
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import com.example.data.model.ReminderSchedule
import com.example.data.model.RitualLog
import com.example.data.model.SettingsEntity
import com.example.data.model.StruggleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SettingsEntity::class,
        LadderItem::class,
        ErpSession::class,
        ImaginalScript::class,
        RitualLog::class,
        StruggleEvent::class,
        PersonalGoal::class,
        ReminderSchedule::class
    ],
    version = 2,
    exportSchema = true
)
abstract class SteadyDatabase : RoomDatabase() {
    abstract fun steadyDao(): SteadyDao

    companion object {
        @Volatile
        private var INSTANCE: SteadyDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new columns to ladder_items
                db.execSQL("ALTER TABLE ladder_items ADD COLUMN personalGoalId INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE ladder_items ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")

                // Add new columns to erp_sessions
                db.execSQL("ALTER TABLE erp_sessions ADD COLUMN compulsionResisted TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE erp_sessions ADD COLUMN alternativeAction TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE erp_sessions ADD COLUMN wasCompleted INTEGER NOT NULL DEFAULT 1")

                // Add new columns to settings
                db.execSQL("ALTER TABLE settings ADD COLUMN allowNotifications INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE settings ADD COLUMN demoDataLoaded INTEGER NOT NULL DEFAULT 0")

                // Create personal_goals table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS personal_goals (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL DEFAULT '',
                        category TEXT NOT NULL DEFAULT 'Daily Living',
                        isActive INTEGER NOT NULL DEFAULT 1,
                        daysPracticedCount INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        lastPracticedDate INTEGER DEFAULT NULL
                    )
                """.trimIndent())

                // Create reminder_schedules table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS reminder_schedules (
                        id INTEGER PRIMARY KEY NOT NULL,
                        isEnabled INTEGER NOT NULL DEFAULT 0,
                        timeHour INTEGER NOT NULL DEFAULT 20,
                        timeMinute INTEGER NOT NULL DEFAULT 0,
                        weekdaysOnly INTEGER NOT NULL DEFAULT 0,
                        includeWeekends INTEGER NOT NULL DEFAULT 1,
                        lastNotificationTimestamp INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): SteadyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SteadyDatabase::class.java,
                    "steady_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(SteadyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SteadyDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Fresh clean install: only initialize settings with hasCompletedOnboarding = false
                    database.steadyDao().saveSettings(
                        SettingsEntity(
                            id = 1,
                            hasCompletedOnboarding = false,
                            userName = "",
                            ocdThemes = "",
                            reminderTime = "20:00",
                            isDarkTheme = null,
                            reducedMotion = false,
                            largeText = false,
                            aiCoachEnabled = false,
                            aiCoachConsentAccepted = false,
                            language = "en",
                            allowNotifications = false,
                            demoDataLoaded = false
                        )
                    )
                    database.steadyDao().saveReminderSchedule(
                        ReminderSchedule(
                            id = 1,
                            isEnabled = false,
                            timeHour = 20,
                            timeMinute = 0,
                            weekdaysOnly = false,
                            includeWeekends = true
                        )
                    )
                }
            }
        }
    }
}

/**
 * Optional demo sample data loader for clinical review or testing.
 * Only loaded when explicitly requested by the user from Settings.
 */
suspend fun populateSamplePracticeData(dao: SteadyDao) {
    val now = System.currentTimeMillis()
    val dayMs = 86_400_000L

    // Personal Goals
    val goal1 = dao.insertGoal(
        PersonalGoal(
            title = "Leaving home without checking locks repeatedly",
            description = "Spend less time at the front door so I can leave for work without anxiety delays.",
            category = "Daily Living",
            isActive = true,
            daysPracticedCount = 5,
            createdAt = now - dayMs * 14,
            lastPracticedDate = now - dayMs * 1
        )
    )

    val goal2 = dao.insertGoal(
        PersonalGoal(
            title = "Enjoying meals with friends without excessive hygiene rituals",
            description = "Be present with friends without constantly worrying about surface contamination.",
            category = "Relationships",
            isActive = true,
            daysPracticedCount = 3,
            createdAt = now - dayMs * 10,
            lastPracticedDate = now - dayMs * 2
        )
    )

    // Pre-populate Ladder Items (sorted by SUDS)
    val item1 = dao.insertLadderItem(
        LadderItem(
            title = "Touching public door handle & waiting 15 min before washing",
            category = "Contamination",
            baselineSuds = 75,
            status = "IN_PROGRESS",
            notes = "Do not wipe hands on clothes. Allow uncomfortable physical sensation to rise and peak.",
            orderIndex = 0,
            createdAt = now - dayMs * 10,
            sessionCount = 3,
            lastSuds = 35,
            personalGoalId = goal2
        )
    )

    val item2 = dao.insertLadderItem(
        LadderItem(
            title = "Leaving apartment after checking front door lock only once",
            category = "Checking",
            baselineSuds = 65,
            status = "IN_PROGRESS",
            notes = "Check once with focused attention, then walk to elevator without turning around.",
            orderIndex = 1,
            createdAt = now - dayMs * 9,
            sessionCount = 2,
            lastSuds = 30,
            personalGoalId = goal1
        )
    )

    val item3 = dao.insertLadderItem(
        LadderItem(
            title = "Writing down feared intrusive thought and reading it aloud",
            category = "Unwanted Thoughts",
            baselineSuds = 85,
            status = "NOT_STARTED",
            notes = "Stay with uncertainty. Do not neutralize with a 'safe' thought.",
            orderIndex = 2,
            createdAt = now - dayMs * 8,
            sessionCount = 0,
            lastSuds = null
        )
    )

    val item4 = dao.insertLadderItem(
        LadderItem(
            title = "Leaving kitchen counter slightly misaligned / crooked napkin holder",
            category = "Symmetry / Order",
            baselineSuds = 40,
            status = "MASTERED",
            notes = "Leave it tilted for the entire evening. The urge passed after 20 minutes.",
            orderIndex = 3,
            createdAt = now - dayMs * 14,
            sessionCount = 4,
            lastSuds = 15
        )
    )

    val item5 = dao.insertLadderItem(
        LadderItem(
            title = "Preparing food without asking partner 'Did you wash your hands?'",
            category = "Contamination",
            baselineSuds = 50,
            status = "NOT_STARTED",
            notes = "Reassurance-seeking feeds the cycle. Tolerate the discomfort.",
            orderIndex = 4,
            createdAt = now - dayMs * 5,
            sessionCount = 0,
            lastSuds = null
        )
    )

    // Pre-populate ERP Sessions with distress curves & inhibitory learning notes
    dao.insertSession(
        ErpSession(
            ladderItemId = item1,
            ladderItemTitle = "Touching public door handle & waiting 15 min before washing",
            sessionType = "IN_VIVO",
            predictionText = "I will feel overwhelmed and contaminate my personal phone.",
            predictionBeliefStart = 85,
            startSuds = 80,
            checkInsJson = "0:80,3:75,6:60,9:45,12:35,15:25",
            endSuds = 25,
            predictionBeliefEnd = 20,
            learnedNote = "The peak lasted about 6 minutes, then leveled off naturally without hand sanitizer.",
            durationMinutes = 15,
            timestamp = now - dayMs * 2,
            compulsionResisted = "Washing hands with sanitizing gel immediately",
            alternativeAction = "Kept hands on lap and breathed naturally",
            wasCompleted = true
        )
    )

    dao.insertSession(
        ErpSession(
            ladderItemId = item2,
            ladderItemTitle = "Leaving apartment after checking front door lock only once",
            sessionType = "IN_VIVO",
            predictionText = "The door might be unlocked and someone will walk in.",
            predictionBeliefStart = 70,
            startSuds = 65,
            checkInsJson = "0:65,3:60,6:50,9:35,12:30",
            endSuds = 30,
            predictionBeliefEnd = 15,
            learnedNote = "I felt the spike on the staircase, but by the time I walked down the street it subsided.",
            durationMinutes = 12,
            timestamp = now - dayMs * 1,
            compulsionResisted = "Turning around 3 times to rattle handle",
            alternativeAction = "Walked continuously toward the transit stop",
            wasCompleted = true
        )
    )

    // Pre-populate Ritual Logs
    dao.insertRitualLog(
        RitualLog(
            description = "Resisted washing hands after touching mail delivery",
            trigger = "Package from mailbox",
            durationMinutes = 15,
            outcome = "RESISTED",
            timestamp = now - dayMs * 1
        )
    )

    dao.insertRitualLog(
        RitualLog(
            description = "Delayed checking gas burner by 10 minutes",
            trigger = "Thought: 'Did I turn off stove?'",
            durationMinutes = 10,
            outcome = "DELAYED",
            timestamp = now - dayMs * 2
        )
    )

    dao.insertRitualLog(
        RitualLog(
            description = "Resisted texting friend for reassurance about conversation",
            trigger = "Fear that I offended someone",
            durationMinutes = 20,
            outcome = "RESISTED",
            timestamp = now - dayMs * 3
        )
    )

    dao.insertRitualLog(
        RitualLog(
            description = "Repeated light switch check twice before stopping",
            trigger = "Uneasy feeling before leaving bedroom",
            durationMinutes = 5,
            outcome = "PERFORMED",
            timestamp = now - dayMs * 4
        )
    )

    // Pre-populate Imaginal Script
    dao.insertImaginalScript(
        ImaginalScript(
            title = "Contamination uncertainty at grocery store",
            text = "I am touching vegetables at the market. I don't know who touched them before me. My mind tells me I must sanitize immediately. I choose to stay in the store, breathe, and accept that 100% certainty is not possible. I continue shopping with this feeling present.",
            createdAt = now - dayMs * 3
        )
    )

    // Pre-populate Struggle Event
    dao.insertStruggleEvent(
        StruggleEvent(
            struggleType = "RITUAL_URGE",
            startSuds = 80,
            endSuds = 45,
            delayMinutes = 10,
            note = "Waited 10 minutes using the delay timer and breathing circle. Urge subsided from intense to mild.",
            timestamp = now - dayMs * 1
        )
    )

    // Mark demo loaded in settings
    val currentSettings = dao.getSettings() ?: SettingsEntity()
    dao.saveSettings(
        currentSettings.copy(
            hasCompletedOnboarding = true,
            userName = if (currentSettings.userName.isBlank()) "Alex" else currentSettings.userName,
            ocdThemes = "Contamination, Checking, Unwanted Thoughts",
            demoDataLoaded = true
        )
    )
}
