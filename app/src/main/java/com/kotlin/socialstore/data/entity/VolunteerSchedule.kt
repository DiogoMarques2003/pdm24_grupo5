package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time

@Entity(tableName = "volunteerSchedule")
data class VolunteerSchedule(
    @PrimaryKey(autoGenerate = false) val id: String,
    val userID: String,
    val day: Date,
    val startTime: Time,
    val endTime: Time,
    val accepted: Boolean = false,
    val localId: String?,
    val workFunction: String?
)
