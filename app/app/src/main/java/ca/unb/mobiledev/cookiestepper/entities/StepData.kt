package ca.unb.mobiledev.cookiestepper.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
/** Defines StepData table for daily step counts
 * using data (YYYY-MM-DD) as primary key
 */

@Entity(tableName = "step_table")
data class StepData (
    @PrimaryKey
    @ColumnInfo(name = "record_date")
    val date: String,

    //column to store total steps recorded for that date.
    @ColumnInfo(name = "total_steps")
    val steps: Int,

    //column to store total distance for that date
    @ColumnInfo(name = "total_distance_meters")
    val distance: Float,

    //calories burned for that date
    @ColumnInfo(name = "active_calories_kcal")
    val caloriesBurned: Float
)