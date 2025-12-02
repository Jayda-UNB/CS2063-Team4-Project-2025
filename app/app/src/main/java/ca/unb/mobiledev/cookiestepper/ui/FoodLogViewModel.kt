package ca.unb.mobiledev.cookiestepper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ca.unb.mobiledev.cookiestepper.db.AppDatabase
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry
import ca.unb.mobiledev.cookiestepper.repositories.FoodLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Food log view model
 */
class FoodLogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FoodLogRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        val dao = db.foodLogDao()
        repository = FoodLogRepository(dao)
    }

    //Function to get all entries for a specific day
    fun getEntriesForDate(date: String): LiveData<List<FoodLogEntry>> = repository.getEntriesForDate(date)

    //Function to get total calories for a specific day
    fun getTotalCaloriesForDate(date: String): LiveData<Double> = repository.getTotalCaloriesForDate(date)

    //Function to get all entries, temporary helper
    fun getAll(): LiveData<List<FoodLogEntry>> = repository.getAll()

    //Insert 1 entry into database
    fun insert(entry: FoodLogEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(entry)
        }
    }

    //Delete 1 entry from database
    fun delete(entry: FoodLogEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(entry)
        }
    }

    //Delete all entries from database
    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
        }
    }
}