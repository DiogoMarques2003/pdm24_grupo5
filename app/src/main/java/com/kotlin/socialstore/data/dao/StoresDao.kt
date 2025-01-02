package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Stores
import kotlinx.coroutines.flow.Flow

@Dao
interface StoresDao {
    @Insert
    suspend fun insert(stores: Stores)

    @Insert
    suspend fun insertList(stores: List<Stores>)

    @Query("SELECT * FROM stores")
    fun getAll(): Flow<List<Stores>>

    @Query("SELECT * FROM stores WHERE id = :id")
    fun getById(id: String): Flow<Stores>

    @Delete
    suspend fun delete(stores: Stores)

    @Query("DELETE FROM stores")
    suspend fun deleteAll()
}