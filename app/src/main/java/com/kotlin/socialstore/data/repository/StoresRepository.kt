package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.StoresDao
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Stores
import kotlinx.coroutines.flow.Flow

class StoresRepository(private val storesDao: StoresDao) {
    val allStores: Flow<List<Stores>> = storesDao.getAll()

    suspend fun insert(stores: Stores) {
        storesDao.insert(stores)
    }

    suspend fun insertList(stores: List<Stores>){
        storesDao.insertList(stores)
    }

    suspend fun deleteAll(){
        storesDao.deleteAll()
    }
}