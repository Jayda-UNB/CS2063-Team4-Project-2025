package ca.unb.mobiledev.cookiestepper.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import ca.unb.mobiledev.cookiestepper.entities.StepData
import ca.unb.mobiledev.cookiestepper.entities.UserProfile
import androidx.lifecycle.viewModelScope
import ca.unb.mobiledev.cookiestepper.repositories.StepRepository
import kotlinx.coroutines.launch

class StepViewModel (private val repository: StepRepository): ViewModel(){

    //live data for UI observation

    //total steps taken across all time
    val totalStepsLifetime: LiveData<Int?> = repository.totalStepsLifetime.asLiveData()

    //daily step data for current day
    val todayStepData: LiveData<StepData?> = repository.getTodayStepDataFlow().asLiveData()

    //user's profile
    val userProfile: LiveData<UserProfile?> = repository.userProfile.asLiveData()

    //last 7 days of data
    val lastSevenDaysData: LiveData<List<StepData>> = repository.lastSevenDaysData.asLiveData()

    //write operations

    fun saveUserProfile(heightCm: Float, weightKg: Float) = viewModelScope.launch{
        repository.saveUserProfile(heightCm, weightKg)
    }

    fun recordNewSteps(steps: Int) = viewModelScope.launch{
        repository.processAndInsertNewStepRecord(steps)
    }

    //--testing utility---
    fun simulateStepsUpdate(steps:Int) = viewModelScope.launch {
        repository.processAndInsertNewStepRecord(steps)
    }

    //Factory class required to instantiate the ViewModel with the Repository dependency.
    class Factory(private val repository: StepRepository) : ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if(modelClass.isAssignableFrom(StepViewModel::class.java)){
                return StepViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}