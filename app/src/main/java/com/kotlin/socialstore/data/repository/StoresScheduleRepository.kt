package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.StoresScheduleDao
import com.kotlin.socialstore.data.entity.StoresSchedule
import kotlinx.coroutines.flow.Flow

class StoresScheduleRepository(private val storesScheduleDao: StoresScheduleDao) {
    val allStoresSchedule: Flow<List<StoresSchedule>> = storesScheduleDao.getAll()

    suspend fun insert(storesSchedule: StoresSchedule) {
        storesScheduleDao.insert(storesSchedule)
    }
}