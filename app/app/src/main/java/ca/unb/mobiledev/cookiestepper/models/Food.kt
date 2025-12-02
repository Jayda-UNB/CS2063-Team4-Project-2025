package ca.unb.mobiledev.cookiestepper.models

/**
 * This makes use of the data class pattern.  All values are public.
 * Represents a Food item
 */
data class Food(val fdcId: Int,
                val kcalPer100g: Double,
                val description: String,
                val portion: ArrayList<FoodPortion>) {

    //Function to calculate calories based on the amount of grams the food is
    fun caloriesFromGrams(grams: Double): Double {
        return kcalPer100g * (grams / 100)
    }

    //Function to calculate calories based on the portion/s eaten
    fun caloriesFromPortion(portion: FoodPortion, numUnits: Double): Double {
        val gramsPerUnit = portion.gramWeight / portion.amount
        val totalGrams = gramsPerUnit * numUnits
        return caloriesFromGrams(totalGrams)
    }
}

/**
 * Represents a Food Portion
 */
data class FoodPortion(
    val id: Int,
    val amount: Double,
    val gramWeight: Double,
    val unitName: String,
    val unitAbbrev: String
)