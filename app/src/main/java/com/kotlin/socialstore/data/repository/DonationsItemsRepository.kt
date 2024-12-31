package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.DonationsItemsDao
import com.kotlin.socialstore.data.entity.DonationsItems
import kotlinx.coroutines.flow.Flow

class DonationsItemsRepository(private val donationsItemsDao: DonationsItemsDao) {
    val allDonationsItems: Flow<List<DonationsItems>> = donationsItemsDao.getAll()

    suspend fun insert(donationsItems: DonationsItems) {
        donationsItemsDao.insert(donationsItems)
    }
}