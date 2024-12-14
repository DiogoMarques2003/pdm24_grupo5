package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "familyHousehold")
data class FamilyHousehold(
    @PrimaryKey(autoGenerate = false) val id: String,
    val accessKids: Boolean = false
)
