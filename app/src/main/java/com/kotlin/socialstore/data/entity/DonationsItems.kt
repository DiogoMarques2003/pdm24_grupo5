package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donationsItems")
data class DonationsItems(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val donationID: Int,
    val categoryID: Int?,
    val picture: String?,
    val state: String,
    val size: String?,
    val description: String?,
    val quantity: Int
)
