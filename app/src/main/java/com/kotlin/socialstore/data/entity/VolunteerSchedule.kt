package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "volunteerSchedule")
data class VolunteerSchedule(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val userID: Int,
    val day: Date,
    val startTime: Time,
    val endTime: Time,
    val accepted: Boolean = false,
    val local: String?,
    val workFunction: String?
)
