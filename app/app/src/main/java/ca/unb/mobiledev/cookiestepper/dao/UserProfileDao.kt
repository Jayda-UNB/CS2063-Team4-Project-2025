package ca.unb.mobiledev.cookiestepper.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.unb.mobiledev.cookiestepper.entities.UserProfile
import kotlinx.coroutines.flow.Flow

//DAO for user profile entitiy (height/weight)

@Dao
interface UserProfileDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    //query to get single userprofile as a record
    @Query("SELECT * FROM user_profile_table WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    //query to get user's weight
    @Query("SELECT user_weight_kg FROM user_profile_table WHERE id = 1")
    suspend fun getWeightKg(): Float?

    //query to get user's height
    @Query("SELECT user_height_cm FROM user_profile_table WHERE id = 1")
    suspend fun getHeightCm(): Float?
}