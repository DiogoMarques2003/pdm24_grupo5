package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.UsersDao
import com.kotlin.socialstore.data.entity.Users
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class UsersRepository(private val usersDao: UsersDao) {
    val allUsers: Flow<List<Users>> = usersDao.getAll()

    suspend fun insert(users: Users) {
        // Validar se o user existe, se existir apagar
        if (usersDao.existsById(users.id)) {
            usersDao.delete(users)
        }

        // Inserir o utilizador atualizado
        usersDao.insert(users)
    }

    fun getById(id: String): Flow<Users> {
        return usersDao.getById(id)
    }
    suspend fun deleteById(id: String) {
        usersDao.deleteById(id)
    }
}