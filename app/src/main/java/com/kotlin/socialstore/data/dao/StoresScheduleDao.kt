package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kotlin.socialstore.data.entity.StoresSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface StoresScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(storesSchedule: StoresSchedule)

    @Query("SELECT * FROM storesSchedule")
    fun getAll(): Flow<List<StoresSchedule>>

    @Query("SELECT * FROM storesSchedule WHERE id = :id")
    fun getById(id: String): Flow<StoresSchedule>

    @Delete
    suspend fun delete(storesSchedule: StoresSchedule)
}