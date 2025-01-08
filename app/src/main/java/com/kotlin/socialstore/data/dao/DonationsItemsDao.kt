package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kotlin.socialstore.data.entity.DonationsItems
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationsItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donationsItems: DonationsItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(donationsItems: List<DonationsItems>)

    @Query("SELECT * FROM donationsItems")
    fun getAll(): Flow<List<DonationsItems>>

    @Query("SELECT * FROM donationsItems WHERE id = :id")
    fun getById(id: String): Flow<DonationsItems>

    @Query("SELECT * FROM donationsItems WHERE donationId = :donationId")
    fun getByDonationId(donationId: String): Flow<List<DonationsItems>>

    @Query("DELETE FROM donationsItems")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(donationsItems: DonationsItems)
}