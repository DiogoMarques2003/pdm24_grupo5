package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DateTimeException

@Entity(tableName = "takenItems")
data class TakenItems(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val familyHouseholdID: Int,
    val categoryID: Int,
    val quantity: Int = 0,
    val date: DateTimeException,
    val voluntierID: Int
)
