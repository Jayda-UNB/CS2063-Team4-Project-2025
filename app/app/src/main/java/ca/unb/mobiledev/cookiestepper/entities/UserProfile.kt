package ca.unb.mobiledev.cookiestepper.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/*
User's personal profile data(weight, height) needed
for local distance and calorie calculations
 */

@Entity(tableName = "user_profile_table")
data class UserProfile(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,

    @ColumnInfo(name = "user_weight_kg")
    val weightKg : Float,

    @ColumnInfo(name = "user_height_cm")
    val heightCm: Float,

    )