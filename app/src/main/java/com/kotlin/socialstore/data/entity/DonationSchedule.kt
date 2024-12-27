package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "donationSchedule")
data class DonationSchedule(
    @PrimaryKey(autoGenerate = false) val id: String,
    val local: String,
    val weekDay: Int,
    val startTime: Time,
    val endTime: Time
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "local" to local,
            "weekDay" to weekDay,
            "startTime" to startTime.toString(),
            "endTime" to endTime.toString()
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): DonationSchedule {
            return DonationSchedule(
                id = data["id"] as String,
                local = data["local"] as String,
                weekDay = data["weekDay"] as Int,
                startTime = Time.valueOf(data["startTime"] as String),
                endTime = Time.valueOf(data["endTime"] as String)
            )
        }
    }
}
