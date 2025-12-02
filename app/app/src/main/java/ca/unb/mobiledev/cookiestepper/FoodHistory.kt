package ca.unb.mobiledev.cookiestepper

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.cookiestepper.entities.FoodLogEntry
import ca.unb.mobiledev.cookiestepper.ui.FoodLogViewModel
import java.time.LocalDate

class FoodHistory : AppCompatActivity() {

    private lateinit var foodViewModel: FoodLogViewModel
    private lateinit var adapter: FoodHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_food_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.food_history)) { v, insets ->
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

        resultView = findViewById(R.id.historyTextView)
        resultView.text = "Loading..."

        foodViewModel = ViewModelProvider(this)[FoodLogViewModel::class.java]

        val today = LocalDate.now().toString()

        foodViewModel.getEntriesForDate(today).observe(this) {
            entries -> updateListView(entries)
        }


    }

    private fun updateListView(entries: List<FoodLogEntry>) {
        if(entries.isNotEmpty()) {
            recyclerView.adapter = FoodHistoryAdapter(entries) { entry ->
                //do later
            }
            resultView.text = getString(R.string.num_of_food_results, entries.size)
        }
        else {
            recyclerView.adapter = FoodHistoryAdapter(emptyList()) { }
            resultView.text = getString(R.string.num_of_food_results, 0)
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