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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop the old donationSchedule table if it exists
        database.execSQL("DROP TABLE IF EXISTS donationSchedule")

        // Create the new donationSchedule table with the updated schema
        database.execSQL("""
            CREATE TABLE donationSchedule (
                id TEXT NOT NULL PRIMARY KEY,
                local TEXT NOT NULL,
                weekDay INTEGER NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL
            )
        """.trimIndent())
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop the old donationSchedule table if it exists
        database.execSQL("DROP TABLE IF EXISTS donationsItems")

        // Create the donationsItems table with the required schema
        database.execSQL("""
            CREATE TABLE donationsItems (
                id TEXT NOT NULL PRIMARY KEY,
                donationID TEXT NOT NULL,
                categoryID TEXT NOT NULL,
                picture TEXT,
                state TEXT NOT NULL,
                size TEXT,
                description TEXT,
                quantity INTEGER NOT NULL
            )
        """.trimIndent())
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop the old volunteerSchedule table if it exists
        database.execSQL("DROP TABLE IF EXISTS volunteerSchedule")

        // Create the volunteerSchedule table with the required schema
        database.execSQL("""
            CREATE TABLE volunteerSchedule (
                id TEXT NOT NULL PRIMARY KEY,
                userID TEXT NOT NULL,
                day INTEGER NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                accepted INTEGER NOT NULL DEFAULT 0,
                localId TEXT,
                workFunction TEXT
            )
        """.trimIndent())
    }
}
