package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock")
data class Stock(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val categoryID: Int,
    val picture: String?,
    val state: String,
    val size: String?,
    val description: String
)
