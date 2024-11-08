package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.FamilyHousehold
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyHouseholdDao {
    @Insert
    suspend fun insert(familyHousehold: FamilyHousehold)

    @Query("SELECT * FROM familyHousehold")
    fun getAll(): Flow<List<FamilyHousehold>>

    @Query("SELECT * FROM familyHousehold WHERE id = :id")
    fun getById(id: Int): Flow<FamilyHousehold>

    @Delete
    suspend fun delete(familyHousehold: FamilyHousehold)
}