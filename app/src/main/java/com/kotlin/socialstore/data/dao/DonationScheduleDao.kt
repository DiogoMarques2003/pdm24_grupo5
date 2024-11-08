package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.DonationSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationScheduleDao {
    @Insert
    suspend fun insert(donationSchedule: DonationSchedule)

    @Query("SELECT * FROM donationSchedule")
    fun getAll(): Flow<List<DonationSchedule>>

    @Query("SELECT * FROM donationSchedule WHERE id = :id")
    fun getByID(id: Int): Flow<DonationSchedule>

    @Delete
    suspend fun delete(donationSchedule: DonationSchedule)
}