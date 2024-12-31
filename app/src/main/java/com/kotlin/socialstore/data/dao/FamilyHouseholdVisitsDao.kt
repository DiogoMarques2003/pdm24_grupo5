package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyHouseholdVisitsDao {
    @Insert
    suspend fun insert(familyHouseholdVisits: FamilyHouseholdVisits)

    @Query("SELECT * FROM familyHouseholdVisits")
    fun getAll(): Flow<List<FamilyHouseholdVisits>>

    @Query("SELECT * FROM familyHouseholdVisits")
    fun getAllMonthly(): Flow<List<FamilyHouseholdVisits>>

    @Query("SELECT * FROM familyHouseholdVisits WHERE id = :id")
    fun getById(id: String): Flow<FamilyHouseholdVisits>

    @Delete
    suspend fun delete(familyHouseholdVisits: FamilyHouseholdVisits)
}