package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(tableName = "takenItems")
data class TakenItems(
    @PrimaryKey(autoGenerate = false) val id: String,
    val familyHouseholdID: String,
    val categoryID: String,
    val quantity: Int = 0,
    val date: Date,
    val voluntierID: String
)
