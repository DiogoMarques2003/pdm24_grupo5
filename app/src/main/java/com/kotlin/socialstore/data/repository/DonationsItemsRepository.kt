package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.DonationsItemsDao
import com.kotlin.socialstore.data.entity.DonationsItems
import kotlinx.coroutines.flow.Flow

class DonationsItemsRepository(private val donationsItemsDao: DonationsItemsDao) {
    val allDonationsItems: Flow<List<DonationsItems>> = donationsItemsDao.getAll()

    suspend fun insert(donationsItems: DonationsItems) {
        donationsItemsDao.insert(donationsItems)
    }

    suspend fun insertList(donationsItems: List<DonationsItems>) {
        donationsItemsDao.insertList(donationsItems)
    }

    fun getByDonationId(donationId: String): Flow<List<DonationsItems>> {
        return donationsItemsDao.getByDonationId(donationId)
    }

    suspend fun deleteAll() {
        donationsItemsDao.deleteAll()
    }
}