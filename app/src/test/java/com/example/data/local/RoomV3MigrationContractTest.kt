package com.example.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class RoomV3MigrationContractTest {
    @Test
    fun schemaVersionMustAdvanceToThreeForThePlannedDataPreservingMigration() {
        assertEquals(
            "Room v3 must be registered only alongside its tested data-preserving migration",
            3,
            STEADY_DATABASE_VERSION
        )
    }
}
