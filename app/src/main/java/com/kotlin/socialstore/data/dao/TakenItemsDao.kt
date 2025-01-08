package com.kotlin.socialstore.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import com.kotlin.socialstore.data.entity.TakenItems
import com.kotlin.socialstore.data.entity.TakenItemsCategoryCount
import kotlinx.coroutines.flow.Flow

@Dao
interface TakenItemsDao {
    @Insert
    suspend fun insert(takenItems: TakenItems)

    @Insert
    suspend fun inserList(takenItems: List<TakenItems>)

    @Query("SELECT * FROM takenItems")
    fun getAll(): Flow<List<TakenItems>>

    @Query("SELECT * FROM takenItems WHERE id = :id")
    fun getById(id: String): Flow<TakenItems>

    @Query(
        "SELECT SUM(quantity) FROM takenItems WHERE familyHouseholdId = :id  AND date >= strftime('%s', 'now', 'start of month') * 1000 AND date < strftime('%s', 'now', 'start of month', '+1 month') * 1000 GROUP BY familyHouseholdID;"
    )
    fun getTakenItensMonthlyById(id: String): Flow<Long>

    @Query(
        "SELECT SUM(quantity) FROM takenItems WHERE date >= strftime('%s', 'now', 'start of month') * 1000 AND date < strftime('%s', 'now', 'start of month', '+1 month') * 1000 GROUP BY familyHouseholdID;"
    )
    fun getTakenItensMonthly(): Flow<Long>

    @Query("SELECT Sum(quantity) AS quantity, categoryID FROM takenItems AS t INNER JOIN users AS u ON u.id = :userId AND u.familyHouseholdID = t.familyHouseholdID GROUP BY categoryID")
    fun getTakenItemsByHousehold(userId: String): Flow<List<TakenItemsCategoryCount>>

    @Query("DELETE FROM takenItems")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(takenItems: TakenItems)
}