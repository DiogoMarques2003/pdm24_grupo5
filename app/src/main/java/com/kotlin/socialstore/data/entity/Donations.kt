package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
import java.sql.Date

@Entity(tableName = "donations")
data class Donations(
    @PrimaryKey(autoGenerate = false) val id: String,
    val donaterName: String,
    val email: String,
    val phoneNumber: String,
    val phoneCountryCode: String,
    val state: String,
    val donationScheduleID: String,
    val creationDate: Date,
    val donationId: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "donaterName" to donaterName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "phoneCountryCode" to phoneCountryCode,
            "state" to state,
            "donationScheduleID" to donationScheduleID,
            "creationDate" to creationDate,
            "donationId" to donationId
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Donations {
            val donationReference = data["donationScheduleID"] as? DocumentReference

            // Convert date
            val timestamp = data["creationDate"] as com.google.firebase.Timestamp
            val creationDateInMillis = timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000
            val creationDate = Date(creationDateInMillis)

            return Donations(
                id = data["id"] as String,
                donaterName = data["donaterName"] as String,
                email = data["email"] as String,
                phoneNumber = data["phoneNumber"] as String,
                phoneCountryCode = data["phoneCountryCode"] as String,
                state = data["state"] as String,
                donationScheduleID = donationReference?.id ?: "",
                creationDate = creationDate,
                donationId = data["donationId"] as String
            )
        }
    }
}
