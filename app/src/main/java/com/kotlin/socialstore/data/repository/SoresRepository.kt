package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.StoresDao
import com.kotlin.socialstore.data.entity.Stores
import kotlinx.coroutines.flow.Flow

class SoresRepository(private val storesDao: StoresDao) {
    val allStores: Flow<List<Stores>> = storesDao.getAll()

    suspend fun insert(stores: Stores) {
        storesDao.insert(stores)
    }
}