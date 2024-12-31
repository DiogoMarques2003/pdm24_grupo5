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

    @Insert
    suspend fun insertList(familyHouseholdVisits: List<FamilyHouseholdVisits>)

    @Query("SELECT * FROM familyHouseholdVisits")
    fun getAll(): Flow<List<FamilyHouseholdVisits>>

    @Query(
        "SELECT * FROM familyHouseholdVisits WHERE date >= strftime('%s', 'now', 'start of month') * 1000 AND date < strftime('%s', 'now', 'start of month', '+1 month') * 1000;"
    )
    fun getAllMonthly(): Flow<List<FamilyHouseholdVisits>>

    @Query("SELECT * FROM familyHouseholdVisits WHERE id = :id")
    fun getById(id: String): Flow<FamilyHouseholdVisits>

    @Query("DELETE FROM familyHouseholdVisits")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(familyHouseholdVisits: FamilyHouseholdVisits)
}