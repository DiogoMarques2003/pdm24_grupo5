package com.kotlin.socialstore.data.repository

import com.google.firebase.firestore.auth.User
import com.kotlin.socialstore.data.dao.UsersDao
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.entity.UsersNotes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class UsersRepository(private val usersDao: UsersDao) {
    val allUsers: Flow<List<Users>> = usersDao.getAll()

    suspend fun insert(users: Users) {
        usersDao.deleteById(users.id)

        // Inserir o utilizador atualizado
        usersDao.insert(users)
    }

    fun getById(id: String): Flow<Users> {
        return usersDao.getById(id)
    }

    fun getByTypeAccount(accountType: String): Flow<List<Users>>{
        return usersDao.getByTypeAccount(accountType)
    }

    suspend fun deleteById(id: String) {
        usersDao.deleteById(id)
    }

    suspend fun insertList(users: List<Users>){
        usersDao.insertList(users)
    }

    suspend fun deleteAll(){
        usersDao.deleteAll()
    }

    suspend fun deleteByTypeAccount(accountType: String){
        usersDao.deleteByTypeAccount(accountType)
    }

    fun getHouseholdNotes(householdId: String): Flow<List<UsersNotes>> {
        return usersDao.getHouseholdNotes(householdId)
    }
}