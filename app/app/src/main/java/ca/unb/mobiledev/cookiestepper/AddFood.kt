package ca.unb.mobiledev.cookiestepper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobiledev.unb.ca.threadinglab.models.Food
import mobiledev.unb.ca.threadinglab.utils.FoodJsonUtils

class AddFood : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var resultsTextView: TextView

    private var foodsList: List<Food> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_food)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            // This gives us the return arrow button in the action bar
            setDisplayHomeAsUpEnabled(true)
        }


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchEditText = findViewById(R.id.searchEditText)
        resultsTextView = findViewById(R.id.resultsTextView)

        resultsTextView.text = "Loading foods list..."

        lifecycleScope.launch(Dispatchers.IO) {
            val jsonUtils = FoodJsonUtils(applicationContext)
            val foods = jsonUtils.foods

            withContext(Dispatchers.Main) {
                foodsList = foods

                if(foodsList.isNotEmpty()) {
                    updateListView(foodsList)
                    resultsTextView.text = "Showing full list"
                }
                else {
                    updateListView(emptyList())
                    resultsTextView.text = "Empty list"
                }
            }
        }

        searchEditText.setOnEditorActionListener { searchText, actionId, _  ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchText.text.toString()
                searchFoodsByName(query)
                true
            }
            false
        }
    }

    private fun searchFoodsByName(name: String) {
        val trimmed = name.trim()
        if(trimmed.isEmpty()) {
            fullListView()
            return
        }

        val results = foodsList.filter { food ->
            food.description.contains(trimmed, ignoreCase = true)
        }

        if(results.isNotEmpty()) {
            updateListView(results)
            resultsTextView.text = getString(R.string.num_of_food_results, results.size)
        }
        else {
            fullListView()
            Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show()
        }
        //searchEditText.text.clear()
    }

    private fun fullListView() {
        if(foodsList.isNotEmpty()) {
            updateListView(foodsList)
            resultsTextView.text = "Showing full list"
        }
        else {
            updateListView(emptyList())
            resultsTextView.text = "No items found"
        }
    }

    private fun updateListView(items: List<Food>) {
        recyclerView.adapter = FoodAdapter(items) { item ->
            val intent: Intent = Intent(this, FoodDetailActivity::class.java)
            intent.putExtra("food_id", item.fdcId)
            intent.putExtra("food_description", item.description)
            intent.putExtra("food_kcal_per_100g", item.kcalPer100g)

            startActivity(intent)
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
}