package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.TakenItemsDao
import com.kotlin.socialstore.data.entity.TakenItems
import com.kotlin.socialstore.data.entity.TakenItemsCategoryCount
import kotlinx.coroutines.flow.Flow

class TakenItemsRepository(private val takenItemsDao: TakenItemsDao) {
    val allTakenItems: Flow<List<TakenItems>> = takenItemsDao.getAll()

    suspend fun insert(takenItems: TakenItems) {
        takenItemsDao.insert(takenItems)
    }

    suspend fun insertList(takenItems: List<TakenItems>){
        takenItemsDao.inserList(takenItems)
    }

    fun getTakenItensMonthlyById(id: String): Flow<Long>{
        return takenItemsDao.getTakenItensMonthlyById(id)
    }

    fun getTakenItensMonthly(): Flow<Long>{
        return takenItemsDao.getTakenItensMonthly()
    }

    fun getTakenItemsByHousehold(userId: String): Flow<List<TakenItemsCategoryCount>> {
        return takenItemsDao.getTakenItemsByHousehold(userId)
    }

    suspend fun deleteAll(){
        takenItemsDao.deleteAll()
    }
}