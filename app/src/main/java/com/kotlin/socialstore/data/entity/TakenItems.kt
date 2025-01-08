package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.firebase.FirebaseObj
import java.sql.Date


@Entity(tableName = "takenItems")
data class TakenItems(
    @PrimaryKey(autoGenerate = false) val id: String,
    val familyHouseholdID: String,
    val categoryID: String,
    val quantity: Int = 0,
    val date: Date,
    val voluntierID: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "familyHouseholdId" to FirebaseObj.getReferenceById(
                DataConstants.FirebaseCollections.familyHousehold,
                familyHouseholdID
            ),
            "categoryId" to FirebaseObj.getReferenceById(
                DataConstants.FirebaseCollections.category,
                categoryID
            ),
            "quantity" to quantity,
            "date" to date, // Converte a data para timestamp
            "voluntierId" to FirebaseObj.getReferenceById(
                DataConstants.FirebaseCollections.users,
                voluntierID
            )
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): TakenItems {
            val familyHouseholdReference = data["familyHouseholdId"] as? DocumentReference
            val categoryReference = data["categoryId"] as? DocumentReference
            val voluntierReference= data["voluntierId"] as? DocumentReference

            // Convert date
            val timestamp = data["date"] as com.google.firebase.Timestamp
            val dateInMillis = timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000
            val date = Date(dateInMillis)

            //Convert to Long
            val quantity = data["quantity"] as Long

            return TakenItems(
                id = data["id"] as String,
                familyHouseholdID = familyHouseholdReference?.id ?: "",
                categoryID = categoryReference?.id ?: "",
                quantity = quantity.toInt(),
                date = date,
                voluntierID = voluntierReference?.id ?: ""
            )
        }
    }
}
