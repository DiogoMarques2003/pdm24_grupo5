package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = false) val id: String,
    val nome: String,
    val kidsStore: Boolean = false
) {
    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "nome" to nome,
            "kidsStore" to kidsStore
        )
    }

    companion object {
        fun firebaseMapToClass(data: Map<String, Any?>): Category {
            return Category(
                id = data["id"] as String,
                nome = data["nome"] as String,
                kidsStore = data["kidsStore"] as? Boolean ?: false
            )
        }
    }
}
