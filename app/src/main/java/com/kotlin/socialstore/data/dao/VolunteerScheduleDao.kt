package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.VolunteerSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface VolunteerScheduleDao {
    @Insert
    suspend fun insert(volunteerSchedule: VolunteerSchedule)

    @Query("SELECT * FROM volunteerSchedule")
    fun getAll(): Flow<List<VolunteerSchedule>>

    @Query("SELECT * FROM volunteerSchedule WHERE id = :id")
    fun getById(id: String): Flow<VolunteerSchedule>

    @Delete
    suspend fun delete(volunteerSchedule: VolunteerSchedule)
}