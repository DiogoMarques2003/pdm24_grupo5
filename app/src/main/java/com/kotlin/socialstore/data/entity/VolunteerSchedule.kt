package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.firebase.FirebaseObj
import java.sql.Date
import java.sql.Timestamp

@Entity(tableName = "volunteerSchedule")
data class VolunteerSchedule(
    @PrimaryKey(autoGenerate = false) val id: String,
    val userID: String,
    val day: Date,
    val startTime: String,
    val endTime: String,
    var accepted: Boolean = false,
    val localId: String?,
    var workFunction: String?
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "userId" to FirebaseObj.getReferenceById(DataConstants.FirebaseCollections.users, userID),
            "day" to Timestamp(day.time), // Converte a data para timestamp
            "startTime" to startTime.toString(), // Converte Time para String
            "endTime" to endTime.toString(), // Converte Time para String
            "accepted" to accepted,
            "localId" to if (localId.isNullOrEmpty()) null else FirebaseObj.getReferenceById(DataConstants.FirebaseCollections.stores, localId),
            "workFunction" to workFunction
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): VolunteerSchedule {
            val userReference= data["userId"] as? DocumentReference
            val localReference= data["localId"] as? DocumentReference

            // Convert date
            val timestamp = data["day"] as com.google.firebase.Timestamp
            val dateInMillis = timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000
            val date = Date(dateInMillis)

            return VolunteerSchedule(
                id = data["id"] as String,
                userID = userReference?.id ?: "",
                day = date,
                startTime = data["startTime"] as String, // Converte String para Time
                endTime = data["endTime"] as String, // Converte String para Time
                accepted = data["accepted"] as? Boolean ?: false,
                localId = localReference?.id ?: "",
                workFunction = data["workFunction"] as? String
            )
        }
    }
}
