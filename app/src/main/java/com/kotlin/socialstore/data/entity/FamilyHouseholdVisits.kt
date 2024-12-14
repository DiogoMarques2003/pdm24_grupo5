package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "familyHouseholdVisits")
data class FamilyHouseholdVisits(
    @PrimaryKey(autoGenerate = false) val id: String,
    val familyHouseholdId: String,
    val date: Date
)
