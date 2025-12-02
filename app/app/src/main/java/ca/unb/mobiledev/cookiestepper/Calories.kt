package ca.unb.mobiledev.cookiestepper

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.cookiestepper.ui.FoodLogViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.time.LocalDate

/**
 * A simple [Fragment] subclass.
 */
class Calories : Fragment() {
    private lateinit var foodLogViewModel: FoodLogViewModel
    private lateinit var calorieCountText: TextView
    private lateinit var calorieProgressBar: LinearProgressIndicator
    private val dailyCalorieGoal = 2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calories, container, false)
    }

    //Adding UI listeners and other UI elements
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calorieCountText = view.findViewById(R.id.calorieCountText)
        calorieProgressBar = view.findViewById(R.id.calorieProgressBar)

        val addFoodButton = view.findViewById<Button>(R.id.addFoodButton)
        val foodHistoryButton = view.findViewById<Button>(R.id.foodHistoryButton)

        addFoodButton?.setOnClickListener {
            val addFoodIntent = Intent(this.activity, AddFood::class.java)
            startActivity(addFoodIntent)
        }
        foodHistoryButton.setOnClickListener {
            val foodHistoryIntent = Intent(this.activity, FoodHistory::class.java)
            startActivity(foodHistoryIntent)
        }

        foodLogViewModel = ViewModelProvider(requireActivity())[FoodLogViewModel::class.java]
        val today = LocalDate.now().toString()

        foodLogViewModel.getTotalCaloriesForDate(today).observe(viewLifecycleOwner) { total ->
            val consumed = (total ?: 0.0).toInt()
            calorieCountText.text = "$consumed/$dailyCalorieGoal kcal"

            val percent = (consumed * 100 / dailyCalorieGoal).coerceIn(0,100)
            calorieProgressBar.setProgressCompat(percent, true)
        }
    }
}