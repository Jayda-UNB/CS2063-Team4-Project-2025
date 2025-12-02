package ca.unb.mobiledev.cookiestepper.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_log")
data class FoodLogEntry (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val date: String,

    val foodName: String,
    val fdcId: Int? = null,

    val portionCount: Int,
    val kcalPer100g: Double,

    val totalCalories: Double
    )