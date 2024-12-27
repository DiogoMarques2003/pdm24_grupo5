package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "familyHouseholdVisits")
data class FamilyHouseholdVisits(
    @PrimaryKey(autoGenerate = false) val id: String,
    val familyHouseholdId: String,
    val date: Date
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "familyHouseholdId" to familyHouseholdId,
            "date" to date.time // Converte a data para timestamp (milissegundos)
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): FamilyHouseholdVisits {
            return FamilyHouseholdVisits(
                id = data["id"] as String,
                familyHouseholdId = data["familyHouseholdId"] as String,
                date = Date(data["date"] as Long) // Converte timestamp de volta para `Date`
            )
        }
    }
}
