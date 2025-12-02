package ca.unb.mobiledev.cookiestepper.repositories

import androidx.lifecycle.LiveData
import ca.unb.mobiledev.cookiestepper.dao.FoodLogDao
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry

class FoodLogRepository(private val foodLogDao: FoodLogDao) {

    fun getEntriesForDate(date: String): LiveData<List<FoodLogEntry>> = foodLogDao.getEntriesForDate(date)

    fun getTotalCaloriesForDate(date: String): LiveData<Double> = foodLogDao.getTotalCaloriesForDate(date)

    suspend fun insert(entry: FoodLogEntry) {
        foodLogDao.insert(entry)
    }

    fun getAll(): LiveData<List<FoodLogEntry>> = foodLogDao.getAllEntries()

    suspend fun delete(entry: FoodLogEntry) {
        foodLogDao.deleteEntry(entry)
    }

    suspend fun clearAll() {
        foodLogDao.clearAll()
    }
}