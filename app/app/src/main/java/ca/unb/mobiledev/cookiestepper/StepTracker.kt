package ca.unb.mobiledev.cookiestepper

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ca.unb.mobiledev.cookiestepper.databinding.ActivityStepTrackerBinding
class StepTracker : AppCompatActivity() {
    private lateinit var binding : ActivityStepTrackerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStepTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Steps())


        binding.bottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menuSteps -> replaceFragment(Steps())
                R.id.menuCalories -> replaceFragment(Calories())
                R.id.menuBMI -> replaceFragment(BMI())
                R.id.menuRewards -> replaceFragment(Rewards())
                R.id.menuHistory -> replaceFragment(History())

                else -> {

                }

            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }
}