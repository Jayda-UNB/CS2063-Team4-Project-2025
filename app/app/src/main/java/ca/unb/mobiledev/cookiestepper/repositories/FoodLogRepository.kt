package ca.unb.mobiledev.cookiestepper.repositories

import androidx.lifecycle.LiveData
import ca.unb.mobiledev.cookiestepper.dao.FoodLogDao
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry

/**
 * Repository for food log entries using the food log dao
 */
class FoodLogRepository(private val foodLogDao: FoodLogDao) {

    //Function to get all entries for a specific day
    fun getEntriesForDate(date: String): LiveData<List<FoodLogEntry>> = foodLogDao.getEntriesForDate(date)

    //Function to get total calories for a specific day
    fun getTotalCaloriesForDate(date: String): LiveData<Double> = foodLogDao.getTotalCaloriesForDate(date)

    //Function to get all entries, temporary helper
    fun getAll(): LiveData<List<FoodLogEntry>> = foodLogDao.getAllEntries()

    //Insert 1 entry into database
    suspend fun insert(entry: FoodLogEntry) {
        foodLogDao.insert(entry)
    }

    //Delete 1 entry from database
    suspend fun delete(entry: FoodLogEntry) {
        foodLogDao.deleteEntry(entry)
    }

    //Delete all entries from database
    suspend fun clearAll() {
        foodLogDao.clearAll()
    }
}