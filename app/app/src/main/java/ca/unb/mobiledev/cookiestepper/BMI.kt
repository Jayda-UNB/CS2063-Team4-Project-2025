package ca.unb.mobiledev.cookiestepper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BMI.newInstance] factory method to
 * create an instance of this fragment.
 */
class BMI : Fragment() {
    private var height: Double = 0.0
    private var weight: Double = 0.0

    private var steps: Int = 1000

    private var caloriesBurnt: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

            val caloriesRounded = caloriesBurnt.toInt()


            Toast.makeText(
                requireContext(),
                "Height: $height cm, Weight: $weight kg\nCalories Burnt: $caloriesBurnt",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BMI.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BMI().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}