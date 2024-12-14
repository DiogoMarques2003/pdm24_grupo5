package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert
    suspend fun insert(stock: Stock)

    @Query("SELECT * FROM stock")
    fun getAll(): Flow<List<Stock>>

    @Query("SELECT * FROM stock WHERE id = :id")
    fun getById(id: String): Flow<Stock>

    @Delete
    suspend fun delete(stock: Stock)
}