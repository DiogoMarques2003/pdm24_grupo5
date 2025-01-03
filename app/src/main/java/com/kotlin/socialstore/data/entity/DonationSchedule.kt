package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "donationSchedule")
data class DonationSchedule(
    @PrimaryKey(autoGenerate = false) val id: String,
    val local: String,
    val weekDay: Long,
    val startTime: String,
    val endTime: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "local" to local,
            "weekDay" to weekDay,
            "startTime" to startTime,
            "endTime" to endTime
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): DonationSchedule {
            return DonationSchedule(
                id = data["id"] as String,
                local = data["local"] as String,
                weekDay = data["weekDay"] as Long,
                startTime = data["startTime"] as String,
                endTime = data["endTime"] as String,
            )
        }
    }
}
