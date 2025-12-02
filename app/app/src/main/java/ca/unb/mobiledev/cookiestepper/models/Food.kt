package mobiledev.unb.ca.threadinglab.models

/**
 * This makes use of the data class pattern.  All values are public.
 */
data class Food(val fdcId: Int,
                val kcalPer100g: Double,
                val description: String,
                val portion: ArrayList<FoodPortion>) {

    fun caloriesFromGrams(grams: Double): Double {
        return kcalPer100g * (grams / 100)
    }

    fun caloriesFromPortion(portion: FoodPortion, numUnits: Double): Double {
        val gramsPerUnit = portion.gramWeight / portion.amount
        val totalGrams = gramsPerUnit * numUnits
        return caloriesFromGrams(totalGrams)
    }
}
data class FoodPortion(
    val id: Int,
    val amount: Double,
    val gramWeight: Double,
    val unitName: String,
    val unitAbbrev: String
)