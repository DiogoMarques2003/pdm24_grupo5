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
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "storesId" to storesId,
            "weekDay" to weekDay,
            "startTime" to startTime.toString(), // Converte Time para String
            "endTime" to endTime.toString()     // Converte Time para String
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): StoresSchedule {
            return StoresSchedule(
                id = data["id"] as String,
                storesId = data["storesId"] as String,
                weekDay = data["weekDay"] as Int,
                startTime = Time.valueOf(data["startTime"] as String), // Converte String para Time
                endTime = Time.valueOf(data["endTime"] as String)      // Converte String para Time
            )
        }
    }
}
