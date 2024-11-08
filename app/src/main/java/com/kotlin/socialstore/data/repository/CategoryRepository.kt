package com.kotlin.socialstore.data.repository

import com.kotlin.socialstore.data.dao.CategoryDao
import com.kotlin.socialstore.data.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategorys: Flow<List<Category>> = categoryDao.getAll()

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }
}