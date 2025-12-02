package ca.unb.mobiledev.cookiestepper.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
/* Represents a food log entity in the database. */
@Entity(tableName = "food_log")
data class FoodLogEntry (
    //Log ID
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    //Date when food was consumed
    val date: String,

    //Food name
    val foodName: String,

    //Food ID in the database
    val fdcId: Int? = null,

    //Number of portions consumed
    val portionCount: Int,

    //Amount of kcal per 100g
    val kcalPer100g: Double,

    //Total calories of this entry
    val totalCalories: Double
    )