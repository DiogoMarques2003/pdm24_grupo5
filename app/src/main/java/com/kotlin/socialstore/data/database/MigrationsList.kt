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