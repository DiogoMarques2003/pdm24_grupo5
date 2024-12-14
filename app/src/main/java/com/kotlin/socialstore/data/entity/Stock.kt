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
)
