package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class Donations(
    @PrimaryKey(autoGenerate = false) val id: String,
    val donaterName: String,
    val email: String,
    val phoneNumber: String,
    val phoneCountryCode: String,
    val accepted: Boolean = false,
    val donationScheduleID: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "donaterName" to donaterName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "phoneCountryCode" to phoneCountryCode,
            "accepted" to accepted,
            "donationScheduleID" to donationScheduleID
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Donations {
            return Donations(
                id = data["id"] as String,
                donaterName = data["donaterName"] as String,
                email = data["email"] as String,
                phoneNumber = data["phoneNumber"] as String,
                phoneCountryCode = data["phoneCountryCode"] as String,
                accepted = data["accepted"] as? Boolean ?: false,
                donationScheduleID = data["donationScheduleID"] as String
            )
        }
    }
}
