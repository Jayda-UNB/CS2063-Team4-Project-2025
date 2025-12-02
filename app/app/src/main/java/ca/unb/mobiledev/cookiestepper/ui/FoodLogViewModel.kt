package ca.unb.mobiledev.cookiestepper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ca.unb.mobiledev.cookiestepper.db.AppDatabase
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry
import ca.unb.mobiledev.cookiestepper.repositories.FoodLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodLogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FoodLogRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        val dao = db.foodLogDao()
        repository = FoodLogRepository(dao)
    }

    fun getEntriesForDate(date: String): LiveData<List<FoodLogEntry>> = repository.getEntriesForDate(date)

    fun getTotalCaloriesForDate(date: String): LiveData<Double> = repository.getTotalCaloriesForDate(date)

    fun getAll(): LiveData<List<FoodLogEntry>> = repository.getAll()

    fun insert(entry: FoodLogEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(entry)
        }
    }

    fun delete(entry: FoodLogEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(entry)
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
        }
    }
}