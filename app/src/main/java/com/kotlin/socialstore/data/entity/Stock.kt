package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentReference

@Entity(tableName = "stock")
data class Stock(
    @PrimaryKey(autoGenerate = false) var id: String,
    val categoryID: String,
    var picture: String?,
    val state: String,
    val size: String?,
    val description: String,
    val storesId: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "categoryId" to categoryID,
            "picture" to picture,
            "state" to state,
            "size" to size,
            "description" to description,
            "storesId" to storesId
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Stock {
            val categoryReference = data["categoryId"] as? DocumentReference

            return Stock(
                id = data["id"] as String,
                categoryID = categoryReference?.id ?: "",
                picture = data["picture"] as? String,
                state = data["state"] as String,
                size = data["size"] as? String,
                description = data["description"] as String,
                storesId = data["storesId"] as String
            )
        }
    }
}
