package ca.unb.mobiledev.cookiestepper

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.cookiestepper.dao.FoodLogDao
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry
import ca.unb.mobiledev.cookiestepper.ui.FoodLogViewModel
import java.time.LocalDate


class FoodDetailActivity : AppCompatActivity() {

    private var portionNum : Int = 0
    private var kcalPer100g: Double = 0.0
    private var foodId: Int = 0
    private var foodName: String ?= ""
    private lateinit var viewModel: FoodLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_food_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_food_detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = intent

        foodId = intent.getIntExtra("food_id", -1)
        foodName = intent.getStringExtra("food_description")
        kcalPer100g = intent.getDoubleExtra("food_kcal_per_100g", 0.0)

        viewModel = ViewModelProvider(this)[FoodLogViewModel::class.java]

        // Lookup the action bar as defined in the layout file
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            // This gives us the return arrow button in the action bar
            setDisplayHomeAsUpEnabled(true)
        }
        actionBar?.title = foodName


        val nameText = findViewById<TextView>(R.id.nameTextView)
        nameText.text = foodName

        val descText = findViewById<TextView>(R.id.descriptionTextview)

        val portionText = findViewById<TextView>(R.id.portionTextView)
        portionText.text = getString(R.string.portion_size_text, portionNum)

        var detailText = String.format("%.1f kcal per 100 g", kcalPer100g)
        if (foodId != -1) {
            detailText += "\nFDC ID: $foodId"
        }
        descText.text = detailText
        descText.movementMethod = ScrollingMovementMethod()
        val incButton = findViewById<Button>(R.id.incrementButton)

        incButton?.setOnClickListener {
            incrementPortion(portionText)
        }
        val decButton = findViewById<Button>(R.id.decrementButton)

        decButton?.setOnClickListener {
            decrementPortion(portionText)
        }

        val confirmButton = findViewById<Button>(R.id.confirmFoodButton)

        confirmButton?.setOnClickListener {
            confirmFood()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Destroy the activity and go back to the parent activity
        // This is specified by using android:parentActivityName=".MainActivity" in the
        // AndroidManifest.xml file
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun incrementPortion(portionText: TextView) {
        portionNum++
        portionText.text = getString(R.string.portion_size_text, portionNum)
    }

    fun decrementPortion(portionText: TextView) {
        if(portionNum > 0){
            portionNum--
            portionText.text = getString(R.string.portion_size_text, portionNum)
        }
    }

    fun confirmFood() {
        if(portionNum == 0){
            Toast.makeText(this, "Please select at least 1 portion", Toast.LENGTH_SHORT).show()
            return
        }
        val today = LocalDate.now().toString()

        val totalCalories = portionNum * kcalPer100g

        val entry = FoodLogEntry(
            date = today,
            foodName = foodName ?: "Unknown food",
            fdcId = if(foodId != -1) foodId else null,
            portionCount = portionNum,
            kcalPer100g = kcalPer100g,
            totalCalories = totalCalories
        )

        viewModel.insert(entry)

        Toast.makeText(this, "Food added", Toast.LENGTH_SHORT).show()
        finish()
    }
}