package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference
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
            "id" to id,
            "familyHouseholdID" to familyHouseholdID,
            "categoryID" to categoryID,
            "quantity" to quantity,
            "date" to date.time, // Converte a data para timestamp
            "voluntierID" to voluntierID
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): TakenItems {
            val familyHouseholdReference = data["familyHouseholdID"] as? DocumentReference
            val categoryReference = data["categoryID"] as? DocumentReference
            val voluntierReference= data["voluntierID"] as? DocumentReference

            return TakenItems(
                id = data["id"] as String,
                familyHouseholdID = familyHouseholdReference?.id ?: "",
                categoryID = categoryReference?.id ?: "",
                quantity = data["quantity"] as Int,
                date = Date(data["date"] as Long), // Converte timestamp para Date
                voluntierID = voluntierReference?.id ?: ""
            )
        }
    }
}
