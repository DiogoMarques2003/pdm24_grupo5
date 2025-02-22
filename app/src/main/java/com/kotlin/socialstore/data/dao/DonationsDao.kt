package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kotlin.socialstore.data.entity.Donations
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donations: Donations)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(donations: List<Donations>)

    @Query("SELECT * FROM donations ORDER BY creationDate DESC")
    fun getAll(): Flow<List<Donations>>

    @Query("SELECT * FROM donations WHERE id = :id")
    fun getById(id: String): Flow<Donations>

    @Query("SELECT * FROM donations ORDER BY creationDate DESC LIMIT :number")
    fun getLastDonations(number: Int): Flow<List<Donations>>

    @Delete
    suspend fun delete(donations: Donations)

    @Query("DELETE FROM donations")
    suspend fun deleteAll()
}