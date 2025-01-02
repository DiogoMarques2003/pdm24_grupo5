package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.VolunteerScheduleDao
import com.kotlin.socialstore.data.entity.VolunteerSchedule
import kotlinx.coroutines.flow.Flow

class VolunteerScheduleRepository(private val volunteerScheduleDao: VolunteerScheduleDao) {
    val allVolunteerSchedule: Flow<List<VolunteerSchedule>> = volunteerScheduleDao.getAll()

    suspend fun insert(volunteerSchedule: VolunteerSchedule) {
        volunteerScheduleDao.insert(volunteerSchedule)
    }

    suspend fun insertList(volunteerScheduleList: List<VolunteerSchedule>){
        volunteerScheduleDao.insertList(volunteerScheduleList)
    }

    suspend fun deleteAll(){
        volunteerScheduleDao.deleteAll()
    }
}