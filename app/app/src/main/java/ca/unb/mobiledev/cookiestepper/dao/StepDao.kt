package ca.unb.mobiledev.cookiestepper.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.unb.mobiledev.cookiestepper.entities.StepData
import kotlinx.coroutines.flow.Flow


/**
 * This DAO object validates the SQL at compile-time and associates it with a method
 */
@Dao
interface StepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(step: StepData)

    //query to only get total step count for a single specific date
    @Query("SELECT total_steps FROM step_table WHERE record_date = :date")
    suspend fun getStepsByDate(date: String):Int?

    //query to get all the data (steps, distance and calories) for a specific date as a flow
    //allows the UI to automatically update when a new step total is saved
    @Query(value = "SELECT * FROM step_table WHERE record_date = :date")
    fun getStepDataByDateFlow(date: String): Flow<StepData?>

    //query to calculate total sum of all steps recorded in entrire database
    @Query("SELECT SUM(total_steps) FROM step_table")
    fun getTotalStepsLifetime(): Flow<Int?>

    //query to get records for last 7 days ordered by most recent
    @Query("SELECT * FROM step_table ORDER BY record_Date DESC LIMIT 7")
    fun getLastSevenDays(): Flow<List<StepData>>

    //query to get estimated active calories for a single specific date
    @Query("SELECT active_calories_kcal FROM step_table WHERE record_date = :date")
    suspend fun getCaloriesByDate(date: String): Float?
}