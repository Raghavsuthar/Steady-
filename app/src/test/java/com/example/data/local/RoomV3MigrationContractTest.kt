package com.example.data.local

import androidx.room.Database
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomV3MigrationContractTest {
    @Test
    fun schemaVersionMustAdvanceToThreeForThePlannedDataPreservingMigration() {
        val database = SteadyDatabase::class.java.getAnnotation(Database::class.java)

        assertEquals(
            "Room v3 must be registered only alongside its tested data-preserving migration",
            3,
            database.version
        )
    }
}
