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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Calories.newInstance] factory method to
 * create an instance of this fragment.
 */
class Calories : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var foodLogViewModel: FoodLogViewModel
    private lateinit var calorieCountText: TextView
    private lateinit var calorieProgressBar: LinearProgressIndicator

    private val dailyCalorieGoal = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calories, container, false)
    }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Calories.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Calories().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}