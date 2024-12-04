package com.kotlin.socialstore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class Users (
    @PrimaryKey(autoGenerate = false) val id: Int,
    val email: String,
    val password: String,
    val accountType: String,
    val active: String,
    val name: String,
    val phoneNumber: String,
    val phoneCountryCode: String,
    val profilePic: String?,
    val nationality: String?,
    val reference: String?,
    val notes: String?,
    val warningsLevel: String?,
    val familyHouseholdID: Int?,
    val familyHouseholdVerified: Boolean = false
)