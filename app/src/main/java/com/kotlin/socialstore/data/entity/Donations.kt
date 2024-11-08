package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class Donations(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val donaterName: String,
    val email: String,
    val phoneNumber: String,
    val phoneCountryCode: String,
    val accepted: Boolean = false,
    val donationScheduleID: Int
)
