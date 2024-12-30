package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
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
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userID" to userID,
            "day" to day.time, // Converte a data para timestamp
            "startTime" to startTime.toString(), // Converte Time para String
            "endTime" to endTime.toString(), // Converte Time para String
            "accepted" to accepted,
            "localId" to localId,
            "workFunction" to workFunction
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): VolunteerSchedule {
            val userReference= data["userID"] as? DocumentReference
            val localReference= data["localId"] as? DocumentReference

            return VolunteerSchedule(
                id = data["id"] as String,
                userID = userReference?.id ?: "",
                day = Date(data["day"] as Long), // Converte timestamp para Date
                startTime = Time.valueOf(data["startTime"] as String), // Converte String para Time
                endTime = Time.valueOf(data["endTime"] as String), // Converte String para Time
                accepted = data["accepted"] as? Boolean ?: false,
                localId = localReference?.id ?: "",
                workFunction = data["workFunction"] as? String
            )
        }
    }
}
