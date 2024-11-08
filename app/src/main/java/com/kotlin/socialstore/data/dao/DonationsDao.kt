package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.Donations
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationsDao {
    @Insert
    suspend fun insert(donations: Donations)

    @Query("SELECT * FROM donations")
    fun getAll(): Flow<List<Donations>>

    @Query("SELECT * FROM donations WHERE id = :id")
    fun getById(id: Int): Flow<Donations>

    @Delete
    suspend fun delete(donations: Donations)
}