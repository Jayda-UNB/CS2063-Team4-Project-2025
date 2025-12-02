package ca.unb.mobiledev.cookiestepper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/**
 * A simple [Fragment] subclass.
 */
class BMI : Fragment() {
    private var height: Double = 0.0
    private var weight: Double = 0.0

    private var steps: Int = 1000

    private var caloriesBurnt: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b_m_i, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val heightInput = view.findViewById<EditText>(R.id.editTextNumberDecimal2)
        val weightInput = view.findViewById<EditText>(R.id.editTextNumberDecimal)
        val submitBtn = view.findViewById<Button>(R.id.button)

        submitBtn.setOnClickListener {
            val heightText = heightInput.text.toString()
            val weightText = weightInput.text.toString()

            height = heightText.toDouble()
            weight = weightText.toDouble()
            if(height <= 165) {
                caloriesBurnt = steps * (weight*0.0005)
            }
            else if(165 < height && height < 183) {
                caloriesBurnt = steps * (weight*0.00055)
            }
            else if(height >= 183) {
                caloriesBurnt = steps * (weight*0.0006)
            }

            Toast.makeText(
                requireContext(),
                "Height: $height cm, Weight: $weight kg\nCalories Burnt: $caloriesBurnt",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}