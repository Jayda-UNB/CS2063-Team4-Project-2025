package ca.unb.mobiledev.cookiestepper.repositories


import ca.unb.mobiledev.cookiestepper.dao.StepDao
import ca.unb.mobiledev.cookiestepper.dao.UserProfileDao
import ca.unb.mobiledev.cookiestepper.entities.StepData
import ca.unb.mobiledev.cookiestepper.entities.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StepRepository(
    private val stepDao: StepDao,
    private val userProfileDao: UserProfileDao
) {
    //data streams from the DAO

    //gives live stream of the total steps taken for all the time
    val totalStepsLifetime: Flow<Int?> = stepDao.getTotalStepsLifetime()

    //gives live stream of stepdata onbjects for last 7 days
    val lastSevenDaysData: Flow<List<StepData>> = stepDao.getLastSevenDays()

    //gives live stream of userprofile data (height/weight)
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

    //calculation logic

    /*
    calculate's the estimated stride length based on user's height
    Using a neutral factor (0.414) here to keep it gender neutral
     */
    private fun calculateStrideLengthM(heightCm: Float):Float{
        // Stride Length (m) = Height (cm) * 0.414 / 100
        return (heightCm * 0.414f) /100f
    }

    /*
    calculate distance traveled based on steps and user's height
     */
    fun calculateDistanceKm(steps: Int, heightCm: Float): Float{
        val strideLengthM = calculateStrideLengthM(heightCm)
        val distanceM = steps * strideLengthM
        //convert from meters to km for distance
        return distanceM/1000f
    }

    /*
     calculates active calories burned based on distance and user's weight.
     Formula: Calories (kcal) = Weight (kg) * Distance (km) * Factor
     using a common active calorie factor of 0.65 kcal/kg/km for walking
     */
    fun calculateCalories(weightKg: Float, distanceKm: Float): Float{
        val factor = 0.65f
        return weightKg * distanceKm * factor
    }


    //write operations


    //save user's height and weight to database
    suspend fun saveUserProfile(heightCm: Float, weightKg: Float){
        val profile = UserProfile(
            heightCm = heightCm,
            weightKg = weightKg
        )
        userProfileDao.insertOrUpdate(profile)
    }
    //insert a new stepdata record
    suspend fun processAndInsertNewStepRecord(steps:Int){
        //get user's latest profile data from database
        val profile = userProfile.firstOrNull()
        if(profile == null){
            return
        }

        val distanceKm = calculateDistanceKm(steps, profile.heightCm)
        val calories = calculateCalories(profile.weightKg, distanceKm)
        val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        //Create record with current date
        val newRecord = StepData(
            date = todayDate,
            steps = steps,
            distance = distanceKm,
            caloriesBurned = calories
        )
        stepDao.insertOrUpdate(newRecord)
    }

    //gets stepdata for current day as live stream
    fun getTodayStepDataFlow() :Flow<StepData?>{
        val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        return stepDao.getStepDataByDateFlow(todayDate)
    }


}