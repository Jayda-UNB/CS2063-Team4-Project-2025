package ca.unb.mobiledev.cookiestepper

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class FoodDetailActivity : AppCompatActivity() {


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

        val foodId = intent.getIntExtra("food_id", -1)
        val foodName = intent.getStringExtra("food_name")
        val kcalPer100g = intent.getDoubleExtra("food_kcal_per_100g", 0.0)

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

        var detailText = String.format("%.1f kcal per 100 g", kcalPer100g)
        if (foodId != -1) {
            detailText += "\nFDC ID: $foodId"
        }
        descText.text = detailText
        descText.movementMethod = ScrollingMovementMethod()
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
}