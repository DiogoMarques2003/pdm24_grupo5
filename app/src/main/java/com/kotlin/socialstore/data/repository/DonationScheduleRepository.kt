package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.DonationScheduleDao
import com.kotlin.socialstore.data.entity.DonationSchedule
import kotlinx.coroutines.flow.Flow

class DonationScheduleRepository(private val donationsScheduleDao: DonationScheduleDao) {
    val allDonationSchedule: Flow<List<DonationSchedule>> = donationsScheduleDao.getAll()

    suspend fun insert(donationSchedule: DonationSchedule) {
        donationsScheduleDao.insert(donationSchedule)
    }
}