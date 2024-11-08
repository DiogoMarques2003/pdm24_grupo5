package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.TakenItems
import kotlinx.coroutines.flow.Flow

@Dao
interface TakenItemsDao {
    @Insert
    suspend fun insert(takenItems: TakenItems)

    @Query("SELECT * FROM takenItems")
    fun getAll(): Flow<List<TakenItems>>

    @Query("SELECT * FROM takenItems WHERE id = :id")
    fun getById(id: Int): Flow<TakenItems>

    @Delete
    suspend fun delete(takenItems: TakenItems)
}