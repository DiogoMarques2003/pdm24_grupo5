package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.FamilyHouseholdDao
import com.kotlin.socialstore.data.entity.FamilyHousehold
import kotlinx.coroutines.flow.Flow

class FamilyHouseholdRepository(private val familyHouseholdDao: FamilyHouseholdDao) {
    val allFamilyHousehold: Flow<List<FamilyHousehold>> = familyHouseholdDao.getAll()

    suspend fun insert(familyHousehold: FamilyHousehold) {
        familyHouseholdDao.insert(familyHousehold)
    }
}