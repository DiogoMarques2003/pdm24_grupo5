package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Insert
    suspend fun insert(users: Users)

    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<Users>>

    @Query("SELECT * FROM users WHERE id =:id")
    fun getById(id: String): Flow<Users>

    @Query("SELECT COUNT(*) > 0 FROM users WHERE id = :id")
    suspend fun existsById(id: String): Boolean

    @Delete
    suspend fun delete(users: Users)
}