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
)
