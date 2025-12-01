package ca.unb.mobiledev.cookiestepper.ui


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import ca.unb.mobiledev.cookiestepper.entities.StepData
import ca.unb.mobiledev.cookiestepper.entities.UserProfile
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import ca.unb.mobiledev.cookiestepper.db.AppDatabase
import ca.unb.mobiledev.cookiestepper.repositories.StepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.checkNotNull

class StepViewModel (
    private val repository: StepRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    //secondary constructor for use in StepService as it can't pass savedstatehandle
    constructor(repository: StepRepository):
            this(repository, SavedStateHandle())

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


    //define factory
    companion object{
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T{
                val application = checkNotNull(extras[APPLICATION_KEY] as Application)
                val databaseScope = CoroutineScope(Dispatchers.IO)
                val appDatabase = AppDatabase.getDatabase(application, databaseScope)
                val stepDao = appDatabase.stepDao()
                val userProfileDao = appDatabase.userProfileDao()
                val repository = StepRepository(stepDao, userProfileDao)
                val savedStateHandle = extras.createSavedStateHandle()
                return StepViewModel(
                    repository,
                    savedStateHandle
                ) as T
            }
        }
    }

}