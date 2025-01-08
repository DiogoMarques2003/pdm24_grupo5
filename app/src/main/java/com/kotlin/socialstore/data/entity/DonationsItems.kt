package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.firebase.FirebaseObj

@Entity(tableName = "donationsItems")
data class DonationsItems(
    @PrimaryKey(autoGenerate = false) val id: String,
    val donationID: String,
    val categoryID: String,
    var picture: String?,
    val state: String,
    val size: String?,
    val description: String?,
    val quantity: Int
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "donationID" to FirebaseObj.getReferenceById(DataConstants.FirebaseCollections.donations, donationID),
            "categoryID" to FirebaseObj.getReferenceById(DataConstants.FirebaseCollections.category, categoryID),
            "picture" to picture,
            "state" to state,
            "size" to size,
            "description" to description,
            "quantity" to quantity
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): DonationsItems {
            val donationReference = data["donationID"] as? DocumentReference
            val categoryReference = data["categoryID"] as? DocumentReference

            val quantity = data["quantity"] as Long

            return DonationsItems(
                id = data["id"] as String,
                donationID = donationReference?.id ?: "",
                categoryID = categoryReference?.id ?: "",
                picture = data["picture"] as? String,
                state = data["state"] as String,
                size = data["size"] as? String,
                description = data["description"] as? String,
                quantity = quantity.toInt()
            )
        }
    }
}
