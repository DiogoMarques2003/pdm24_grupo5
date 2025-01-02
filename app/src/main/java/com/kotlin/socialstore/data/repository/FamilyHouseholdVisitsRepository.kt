package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.FamilyHouseholdVisitsDao
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import kotlinx.coroutines.flow.Flow

class FamilyHouseholdVisitsRepository(private val familyHouseholdVisitsDao: FamilyHouseholdVisitsDao) {
    val allFamilyHouseholdVisits: Flow<List<FamilyHouseholdVisits>> = familyHouseholdVisitsDao.getAll()

    suspend fun insert(familyHouseholdVisits: FamilyHouseholdVisits) {
        familyHouseholdVisitsDao.insert(familyHouseholdVisits)
    }

    suspend fun insertList(familyHouseholdVisitsList: List<FamilyHouseholdVisits>){
        familyHouseholdVisitsDao.insertList(familyHouseholdVisitsList)
    }

    fun getAllmonthly(): Flow<List<FamilyHouseholdVisits>>{
        return familyHouseholdVisitsDao.getAllMonthly()
    }

    fun getVisitMonthlyById(id: String): Flow<List<FamilyHouseholdVisits>>{
        return familyHouseholdVisitsDao.getVisitMonthlyById(id)
    }

    suspend fun deleteAll(){
        familyHouseholdVisitsDao.deleteAll()
    }
}