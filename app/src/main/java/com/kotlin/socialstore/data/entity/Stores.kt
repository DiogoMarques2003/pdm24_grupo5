package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class Stores(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val address: String,
    val postalCode: String,
    val city: String
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "address" to address,
            "postalCode" to postalCode,
            "city" to city
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Stores {

            return Stores(
                id = data["id"] as String,
                name = data["name"] as String,
                address = data["address"] as String,
                postalCode = data["postalCode"] as String,
                city = data["city"] as String
            )
        }
    }
}
