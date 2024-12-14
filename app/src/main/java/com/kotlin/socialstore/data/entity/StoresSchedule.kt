package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "storesSchedule")
data class StoresSchedule(
    @PrimaryKey(autoGenerate = false) val id: String,
    val storesId: String,
    val weekDay: Int,
    val startTime: Time,
    val endTime: Time
)
