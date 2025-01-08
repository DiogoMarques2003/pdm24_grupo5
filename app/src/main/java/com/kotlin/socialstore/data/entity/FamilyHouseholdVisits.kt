package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.firebase.FirebaseObj
import java.sql.Date
import java.sql.Timestamp

@Entity(tableName = "familyHouseholdVisits")
data class FamilyHouseholdVisits(
    @PrimaryKey(autoGenerate = false) val id: String,
    val familyHouseholdId: String,
    val date: Date
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "familyHouseholdId" to FirebaseObj.getReferenceById(
                DataConstants.FirebaseCollections.familyHousehold,
                familyHouseholdId
            ),
            "date" to date
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): FamilyHouseholdVisits {
            val familyHouseholdReference = data["familyHouseholdId"] as? DocumentReference

            // Convert date
            val timestamp = data["date"] as com.google.firebase.Timestamp
            val dateInMillis = timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000
            val date = Date(dateInMillis)

            return FamilyHouseholdVisits(
                id = data["id"] as String,
                familyHouseholdId = familyHouseholdReference?.id ?: "",
                date = date
            )
        }
    }
}
