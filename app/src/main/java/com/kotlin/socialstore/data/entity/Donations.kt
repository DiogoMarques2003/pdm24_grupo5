package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference

@Entity(tableName = "donations")
data class Donations(
    @PrimaryKey(autoGenerate = false) val id: String,
    val donaterName: String,
    val email: String,
    val phoneNumber: String,
    val phoneCountryCode: String,
    val state: String,
    val donationScheduleID: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "donaterName" to donaterName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "phoneCountryCode" to phoneCountryCode,
            "state" to state,
            "donationScheduleID" to donationScheduleID
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Donations {
            val donationReference = data["donationScheduleID"] as? DocumentReference

            return Donations(
                id = data["id"] as String,
                donaterName = data["donaterName"] as String,
                email = data["email"] as String,
                phoneNumber = data["phoneNumber"] as String,
                phoneCountryCode = data["phoneCountryCode"] as String,
                state = data["state"] as String,
                donationScheduleID = donationReference?.id ?: ""
            )
        }
    }
}
