package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.DonationsDao
import com.kotlin.socialstore.data.entity.Donations
import kotlinx.coroutines.flow.Flow

class DonationsRepository(private val donationsDao: DonationsDao) {
    val allDonations: Flow<List<Donations>> = donationsDao.getAll()

    suspend fun insert(donations: Donations) {
        donationsDao.insert(donations)
    }

    suspend fun insertList(donations: List<Donations>) {
        donationsDao.insertList(donations)
    }

    suspend fun deleteAll() {
        donationsDao.deleteAll()
    }
}