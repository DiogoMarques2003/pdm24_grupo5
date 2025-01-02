package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "familyHousehold")
data class FamilyHousehold(
    @PrimaryKey(autoGenerate = false) val id: String,
    val accessKids: Boolean = false
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "accessKids" to accessKids
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): FamilyHousehold {
            return FamilyHousehold(
                id = data["id"] as String,
                accessKids = data["accessKids"] as? Boolean ?: false
            )
        }
    }
}
