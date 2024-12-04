package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.TakenItemsDao
import com.kotlin.socialstore.data.entity.TakenItems
import kotlinx.coroutines.flow.Flow

class TakenItemsRepository(private val takenItemsDao: TakenItemsDao) {
    val allTakenItems: Flow<List<TakenItems>> = takenItemsDao.getAll()

    suspend fun insert(takenItems: TakenItems) {
        takenItemsDao.insert(takenItems)
    }
}