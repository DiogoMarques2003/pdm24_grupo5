package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.StockDao
import com.kotlin.socialstore.data.entity.Stock
import kotlinx.coroutines.flow.Flow

class StockRepository(private val stockDao: StockDao) {
    val allStock: Flow<List<Stock>> = stockDao.getAll()

    suspend fun insert(stock: Stock) {
        stockDao.insert(stock)
    }

    suspend fun insertList(stock: List<Stock>){
        stockDao.insertList(stock)
    }

    suspend fun deleteAll(){
        stockDao.deleteAll()
    }
}