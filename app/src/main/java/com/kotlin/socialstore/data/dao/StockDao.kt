package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kotlin.socialstore.data.entity.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(stock: List<Stock>)

    @Query("SELECT * FROM stock")
    fun getAll(): Flow<List<Stock>>

    @Query("SELECT * FROM stock WHERE id = :id")
    fun getById(id: String): Flow<Stock>

    @Query("SELECT * FROM stock ORDER BY id DESC LIMIT :number")
    fun getLastRows(number: Int): Flow<List<Stock>>

    @Delete
    suspend fun delete(stock: Stock)

    @Query("DELETE FROM stock")
    suspend fun deleteAll()
}