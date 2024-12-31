package com.kotlin.socialstore.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kotlin.socialstore.data.Converters.DateConverter
import com.kotlin.socialstore.data.Converters.TimeConverters
import com.kotlin.socialstore.data.dao.CategoryDao
import com.kotlin.socialstore.data.dao.DonationScheduleDao
import com.kotlin.socialstore.data.dao.DonationsDao
import com.kotlin.socialstore.data.dao.DonationsItemsDao
import com.kotlin.socialstore.data.dao.FamilyHouseholdDao
import com.kotlin.socialstore.data.dao.FamilyHouseholdVisitsDao
import com.kotlin.socialstore.data.dao.StockDao
import com.kotlin.socialstore.data.dao.StoresDao
import com.kotlin.socialstore.data.dao.StoresScheduleDao
import com.kotlin.socialstore.data.dao.TakenItemsDao
import com.kotlin.socialstore.data.dao.UsersDao
import com.kotlin.socialstore.data.dao.VolunteerScheduleDao
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.DonationSchedule
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.DonationsItems
import com.kotlin.socialstore.data.entity.FamilyHousehold
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.data.entity.StoresSchedule
import com.kotlin.socialstore.data.entity.TakenItems
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.entity.VolunteerSchedule

@Database(entities = [Category::class, Donations::class, DonationSchedule::class,
                      DonationsItems::class, FamilyHousehold::class, Stock::class,
                      TakenItems::class, Users::class, VolunteerSchedule::class,
                      FamilyHouseholdVisits::class, Stores::class, StoresSchedule::class], version = 3)
@TypeConverters(DateConverter::class, TimeConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun donationScheduleDao(): DonationScheduleDao
    abstract fun donationsDao(): DonationsDao
    abstract fun donationsItemsDao(): DonationsItemsDao
    abstract fun familyHouseholdDao(): FamilyHouseholdDao
    abstract fun stockDao(): StockDao
    abstract fun takenItemsDao(): TakenItemsDao
    abstract fun usersDao(): UsersDao
    abstract fun volunteerScheduleDao(): VolunteerScheduleDao
    abstract fun familyHouseholdVisitsDao(): FamilyHouseholdVisitsDao
    abstract fun storesDao(): StoresDao
    abstract fun storesScheduleDao(): StoresScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app_database")
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}