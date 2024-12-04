package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "donationSchedule")
data class DonationSchedule(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val local: String,
    val weekDay: Int,
    val startTime: Time,
    val endTime: Time
)
