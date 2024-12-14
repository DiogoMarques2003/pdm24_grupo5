package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.FamilyHouseholdVisitsDao
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import kotlinx.coroutines.flow.Flow

class FamilyHouseholdVisitsRepository(private val familyHouseholdVisitsDao: FamilyHouseholdVisitsDao) {
    val allFamilyHouseholdVisits: Flow<List<FamilyHouseholdVisits>> = familyHouseholdVisitsDao.getAll()

    suspend fun insert(familyHouseholdVisits: FamilyHouseholdVisits) {
        familyHouseholdVisitsDao.insert(familyHouseholdVisits)
    }
}