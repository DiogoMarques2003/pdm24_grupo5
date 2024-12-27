package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.UsersDao
import com.kotlin.socialstore.data.entity.Users
import kotlinx.coroutines.flow.Flow

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
}