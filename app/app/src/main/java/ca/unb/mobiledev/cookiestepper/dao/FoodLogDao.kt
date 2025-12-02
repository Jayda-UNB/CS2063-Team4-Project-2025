package ca.unb.mobiledev.cookiestepper.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry

@Dao
interface FoodLogDao {
    //Insert 1 entry into database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FoodLogEntry)

    //Insert all entries into database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FoodLogEntry>)

    //Query to get all entries for a specific day
    @Query("SELECT * FROM food_log WHERE date = :date ORDER BY id DESC")
    fun getEntriesForDate(date: String): LiveData<List<FoodLogEntry>>

    //Query to get all entries, temporary helper
    @Query("SELECT * FROM food_log ORDER BY id DESC")
    fun getAllEntries(): LiveData<List<FoodLogEntry>>

    //Query to get total calories consumed for a specific day
    @Query("SELECT COALESCE(SUM(totalCalories), 0) FROM food_log WHERE date = :date")
    fun getTotalCaloriesForDate(date: String): LiveData<Double>

    //Delete 1 entry from database
    @Delete
    suspend fun deleteEntry(entry: FoodLogEntry)

    //Delete all entries in database
    @Query("DELETE FROM food_log")
    suspend fun clearAll()
}