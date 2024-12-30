package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference

@Entity(tableName = "users")
data class Users (
    @PrimaryKey(autoGenerate = false) val id: String,
    val email: String,
    val accountType: String,
    val active: Boolean = false,
    val name: String,
    val phoneNumber: String,
    val phoneCountryCode: String,
    val profilePic: String?,
    val nationality: String?,
    val reference: String?,
    val notes: String?,
    val warningsLevel: String?,
    val familyHouseholdID: String?,
    val familyHouseholdVerified: Boolean = false
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "accountType" to accountType,
            "active" to active,
            "name" to name,
            "phoneNumber" to phoneNumber,
            "phoneCountryCode" to phoneCountryCode,
            "profilePic" to profilePic,
            "nationality" to nationality,
            "reference" to reference,
            "notes" to notes,
            "warningsLevel" to warningsLevel,
            "familyHouseholdID" to familyHouseholdID,
            "familyHouseholdVerified" to familyHouseholdVerified
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Users {
            val familyHouseholdReference= data["familyHouseholdID"] as? DocumentReference

            return Users(
                id = data["id"] as String,
                email = data["email"] as String,
                accountType = data["accountType"] as String,
                active = data["active"] as Boolean,
                name = data["name"] as String,
                phoneNumber = data["phoneNumber"] as String,
                phoneCountryCode = data["phoneCountryCode"] as String,
                profilePic = data["profilePic"] as? String,
                nationality = data["nationality"] as? String,
                reference = data["reference"] as? String,
                notes = data["notes"] as? String,
                warningsLevel = data["warningsLevel"] as? String,
                familyHouseholdID = familyHouseholdReference?.id ?: "",
                familyHouseholdVerified = data["familyHouseholdVerified"] as? Boolean ?: false
            )
        }
    }
}