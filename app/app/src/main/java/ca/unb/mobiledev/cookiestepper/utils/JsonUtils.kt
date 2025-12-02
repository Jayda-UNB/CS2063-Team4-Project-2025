package mobiledev.unb.ca.threadinglab.utils

import android.content.Context
import android.content.res.AssetManager
import mobiledev.unb.ca.threadinglab.models.Food
import mobiledev.unb.ca.threadinglab.models.FoodPortion
import org.json.JSONObject
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStream
import kotlin.collections.ArrayList

class FoodJsonUtils(context: Context) {
    // Publically accessible list of foods
    // The setter method is private and can only be accessed by this class
    var foods: ArrayList<Food> = ArrayList()
        private set

    private fun processJSON(context: Context) {
        try {
            val jsonString = loadJSONFromAssets(context)
            jsonString?.let { jsonString ->
                val jsonObject = JSONObject(jsonString.trimIndent())

                // Create a JSON Array from the JSON Object
                val foodsArray = jsonObject.getJSONArray(KEY_FOUNDATION_FOODS)
                for (i in 0 until foodsArray.length()) {
                    val foodObj = foodsArray.getJSONObject(i)
                    val fdcId = foodObj.getInt(KEY_FDC_ID)
                    val description = foodObj.getString(KEY_DESCRIPTION)

                    val nutrientsArray = foodObj.getJSONArray(KEY_FOOD_NUTRIENTS)
                    var kcalPer100g = 0.0
                    // Finding nutrient 1008
                    for(j in 0 until nutrientsArray.length()) {
                        val nutrientObj = nutrientsArray.getJSONObject(j)
                        val nutrientInfo = nutrientObj.getJSONObject(KEY_NUTRIENT)
                        val nutrientId = nutrientInfo.getInt(KEY_NUTRIENT_ID)
                        val unitName = nutrientInfo.getString(KEY_UNIT_NAME)
                        if(nutrientId == NUTRIENT_ID_KCAL && unitName == UNIT_KCAL){
                            kcalPer100g = nutrientObj.getDouble(KEY_AMOUNT)
                            break
                        }
                    }
                    val portions = ArrayList<FoodPortion>()

                    if(foodObj.has(KEY_FOOD_PORTIONS)) {
                        val portionsArray = foodObj.getJSONArray(KEY_FOOD_PORTIONS)
                        for(k in 0 until portionsArray.length()) {
                            val portionObj = portionsArray.getJSONObject(k)
                            val portionId = portionObj.getInt(KEY_ID)
                            val amount = portionObj.getDouble(KEY_AMOUNT)
                            val gramWeight = portionObj.getDouble(KEY_GRAM_WEIGHT)

                            val measureObj = portionObj.getJSONObject(KEY_MEASURE_UNIT)
                            val unitName = measureObj.getString(KEY_NAME)
                            val unitAbbr = measureObj.getString(KEY_ABBREVIATION)

                            portions.add(
                                FoodPortion(
                                    id = portionId,
                                    amount = amount,
                                    gramWeight = gramWeight,
                                    unitName = unitName,
                                    unitAbbrev = unitAbbr
                                )
                            )
                        }
                    }

                    val food = Food(
                        fdcId = fdcId,
                        description = description,
                        kcalPer100g = kcalPer100g,
                        portion = portions
                    )
                    foods.add(food)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAssets(context: Context): String? {
        val assets: AssetManager = context.assets

        val inputJSONReader: InputStream = assets.open(FOOD_JSON_FILE)

        val reader = BufferedReader(inputJSONReader.reader())
        var content: String
        try {
            content = reader.readText()
        } finally {
            reader.close()
        }
        return content
    }


    companion object {
        private const val FOOD_JSON_FILE = "FoodJSON"
        private const val KEY_FOUNDATION_FOODS = "FoundationFoods"
        private const val KEY_FDC_ID = "fdcId"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_FOOD_NUTRIENTS = "foodNutrients"
        private const val KEY_NUTRIENT = "nutrient"
        private const val KEY_NUTRIENT_ID = "id"
        private const val KEY_UNIT_NAME = "unitName"
        private const val KEY_AMOUNT = "amount"
        private const val KEY_FOOD_PORTIONS = "foodPortions"
        private const val KEY_ID = "id"
        private const val KEY_GRAM_WEIGHT = "gramWeight"
        private const val KEY_MEASURE_UNIT = "measureUnit"
        private const val KEY_NAME = "name"
        private const val KEY_ABBREVIATION = "abbreviation"
        private const val NUTRIENT_ID_KCAL = 1008
        private const val UNIT_KCAL = "kcal"
    }

    // Initializer to read our data source (JSON file) into an array of food objects
    init {
        processJSON(context)
    }
}