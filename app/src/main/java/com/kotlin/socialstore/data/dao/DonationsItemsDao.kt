package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.DonationsItems
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationsItemsDao {
    @Insert
    suspend fun insert(donationsItems: DonationsItems)

    @Query("SELECT * FROM donationsItems")
    fun getAll(): Flow<List<DonationsItems>>

    @Query("SELECT * FROM donationsItems WHERE id = :id")
    fun getById(id: String): Flow<DonationsItems>

    @Delete
    suspend fun delete(donationsItems: DonationsItems)
}