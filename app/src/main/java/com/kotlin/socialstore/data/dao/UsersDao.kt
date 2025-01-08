package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.google.android.gms.common.internal.AccountType
import com.google.firebase.firestore.auth.User
import com.kotlin.socialstore.data.entity.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Insert
    suspend fun insert(users: Users)

    @Insert
    suspend fun insertList(users: List<Users>)

    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<Users>>

    @Query("SELECT * FROM users WHERE id =:id")
    fun getById(id: String): Flow<Users>

    @Query("SELECT * FROM users WHERE accountType = :accountType")
    fun getByTypeAccount(accountType: String): Flow<List<Users>>

    @Delete
    suspend fun delete(users: Users)

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM users WHERE accountType = :accountType")
    suspend fun deleteByTypeAccount(accountType: String)
}