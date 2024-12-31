package com.kotlin.socialstore.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Excluir tabela antiga
        database.execSQL("DROP TABLE donations")

        // Criar tabela correta
        database.execSQL("""
            CREATE TABLE donations (
                id TEXT NOT NULL PRIMARY KEY,
                donaterName TEXT NOT NULL,
                email TEXT NOT NULL,
                phoneNumber TEXT NOT NULL,
                phoneCountryCode TEXT NOT NULL,
                state TEXT NOT NULL,
                donationScheduleID TEXT NOT NULL
            )
        """.trimIndent())
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop the old donations table
        database.execSQL("DROP TABLE IF EXISTS donations")

        // Create the updated donations table with the new schema
        database.execSQL("""
            CREATE TABLE donations (
                id TEXT NOT NULL PRIMARY KEY,
                donaterName TEXT NOT NULL,
                email TEXT NOT NULL,
                phoneNumber TEXT NOT NULL,
                phoneCountryCode TEXT NOT NULL,
                state TEXT NOT NULL,
                donationScheduleID TEXT NOT NULL,
                creationDate INTEGER NOT NULL,
                donationId TEXT NOT NULL
            )
        """.trimIndent())
    }
}
