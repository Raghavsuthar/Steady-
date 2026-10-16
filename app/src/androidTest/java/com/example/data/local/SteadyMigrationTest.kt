package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SteadyMigrationTest {
    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun versionOneRowsSurviveTheChainedMigrationToVersionThree() {
        val name = "steady-v1-to-v3-test.db"
        createVersionOneDatabase(name)

        val migrated = Room.databaseBuilder(context, SteadyDatabase::class.java, name)
            .addMigrations(SteadyDatabase.MIGRATION_1_2, SteadyDatabase.MIGRATION_2_3)
            .build()

        try {
            val db = migrated.openHelper.writableDatabase
            db.query("SELECT userName, myWhy FROM settings WHERE id = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Existing User", cursor.getString(0))
                assertEquals("", cursor.getString(1))
            }
            db.query("SELECT checkInsJson, learnedNote, wasCompleted FROM erp_sessions WHERE id = 7").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("legacy-checkin-format:0:80,3:75", cursor.getString(0))
                assertEquals("Existing learning note", cursor.getString(1))
                assertEquals(1, cursor.getInt(2))
            }
            db.query("SELECT daysOfWeekMask FROM reminder_schedules WHERE id = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(31, cursor.getInt(0))
            }
        } finally {
            migrated.close()
            context.deleteDatabase(name)
        }
    }

    private fun createVersionOneDatabase(name: String) {
        context.deleteDatabase(name)
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(name)
            .callback(object : SupportSQLiteOpenHelper.Callback(2) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("""CREATE TABLE settings (
                        id INTEGER NOT NULL PRIMARY KEY, hasCompletedOnboarding INTEGER NOT NULL,
                        userName TEXT NOT NULL, ocdThemes TEXT NOT NULL, reminderTime TEXT NOT NULL,
                        isDarkTheme INTEGER, reducedMotion INTEGER NOT NULL, largeText INTEGER NOT NULL,
                        aiCoachEnabled INTEGER NOT NULL, aiCoachConsentAccepted INTEGER NOT NULL,
                        language TEXT NOT NULL)""")
                    db.execSQL("""CREATE TABLE ladder_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL,
                        category TEXT NOT NULL, baselineSuds INTEGER NOT NULL, status TEXT NOT NULL,
                        notes TEXT NOT NULL, orderIndex INTEGER NOT NULL, createdAt INTEGER NOT NULL,
                        sessionCount INTEGER NOT NULL, lastSuds INTEGER)""")
                    db.execSQL("""CREATE TABLE erp_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ladderItemId INTEGER,
                        ladderItemTitle TEXT NOT NULL, sessionType TEXT NOT NULL, predictionText TEXT NOT NULL,
                        predictionBeliefStart INTEGER NOT NULL, startSuds INTEGER NOT NULL,
                        checkInsJson TEXT NOT NULL, endSuds INTEGER NOT NULL, predictionBeliefEnd INTEGER NOT NULL,
                        learnedNote TEXT NOT NULL, durationMinutes INTEGER NOT NULL, timestamp INTEGER NOT NULL)""")
                    db.execSQL("""CREATE TABLE imaginal_scripts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL,
                        text TEXT NOT NULL, audioPath TEXT, createdAt INTEGER NOT NULL)""")
                    db.execSQL("""CREATE TABLE ritual_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, description TEXT NOT NULL,
                        trigger TEXT NOT NULL, durationMinutes INTEGER NOT NULL, outcome TEXT NOT NULL,
                        timestamp INTEGER NOT NULL)""")
                    db.execSQL("""CREATE TABLE struggle_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, struggleType TEXT NOT NULL,
                        startSuds INTEGER NOT NULL, endSuds INTEGER NOT NULL, delayMinutes INTEGER NOT NULL,
                        note TEXT NOT NULL, timestamp INTEGER NOT NULL)""")
                    db.execSQL("INSERT INTO settings VALUES (1, 1, 'Existing User', 'Checking', '20:00', NULL, 0, 0, 0, 0, 'en')")
                    db.execSQL("""INSERT INTO ladder_items
                        (id,title,category,baselineSuds,status,notes,orderIndex,createdAt,sessionCount,lastSuds)
                        VALUES (4,'Legacy step','Checking',60,'IN_PROGRESS','legacy notes',0,1000,2,45)""")
                    db.execSQL("""INSERT INTO erp_sessions
                        (id,ladderItemId,ladderItemTitle,sessionType,predictionText,predictionBeliefStart,startSuds,
                         checkInsJson,endSuds,predictionBeliefEnd,learnedNote,durationMinutes,timestamp)
                        VALUES (7,4,'Legacy step','IN_VIVO','legacy prediction',70,80,
                         'legacy-checkin-format:0:80,3:75',45,50,'Existing learning note',12,1700000000000)""")
                    // Apply v1 -> v2, then seed a legacy reminder row for the v2 -> v3 conversion.
                    SteadyDatabase.MIGRATION_1_2.migrate(db)
                    db.execSQL("""INSERT INTO reminder_schedules
                        (id,isEnabled,timeHour,timeMinute,weekdaysOnly,includeWeekends,lastNotificationTimestamp)
                        VALUES (1,1,20,0,1,0,0)""")
                    db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)")
                    db.execSQL("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, 'ae7df4e7e50fa419fe05863831991808')")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            })
            .build()
        val helper = FrameworkSQLiteOpenHelperFactory().create(configuration)
        helper.writableDatabase
        helper.close()
    }
}