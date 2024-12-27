package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock")
data class Stock(
    @PrimaryKey(autoGenerate = false) val id: String,
    val categoryID: String,
    val picture: String?,
    val state: String,
    val size: String?,
    val description: String,
    val storesId: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "categoryID" to categoryID,
            "picture" to picture,
            "state" to state,
            "size" to size,
            "description" to description,
            "storesId" to storesId
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Stock {
            return Stock(
                id = data["id"] as String,
                categoryID = data["categoryID"] as String,
                picture = data["picture"] as? String,
                state = data["state"] as String,
                size = data["size"] as? String,
                description = data["description"] as String,
                storesId = data["storesId"] as String
            )
        }
    }
}
