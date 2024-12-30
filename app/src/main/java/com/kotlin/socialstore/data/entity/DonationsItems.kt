package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference

@Entity(tableName = "donationsItems")
data class DonationsItems(
    @PrimaryKey(autoGenerate = false) val id: String,
    val donationID: String,
    val categoryID: String?,
    val picture: String?,
    val state: String,
    val size: String?,
    val description: String?,
    val quantity: Int
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "donationID" to donationID,
            "categoryID" to categoryID,
            "picture" to picture,
            "state" to state,
            "size" to size,
            "description" to description,
            "quantity" to quantity
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): DonationsItems {
            val donationReference = data["donationScheduleID"] as? DocumentReference
            val categoryReference = data["categoryID"] as? DocumentReference

            return DonationsItems(
                id = data["id"] as String,
                donationID = donationReference?.id ?: "",
                categoryID = categoryReference?.id ?: "",
                picture = data["picture"] as? String,
                state = data["state"] as String,
                size = data["size"] as? String,
                description = data["description"] as? String,
                quantity = data["quantity"] as Int
            )
        }
    }
}
