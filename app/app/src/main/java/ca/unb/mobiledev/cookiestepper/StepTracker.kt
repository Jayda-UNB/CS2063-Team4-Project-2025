package ca.unb.mobiledev.cookiestepper

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import ca.unb.mobiledev.cookiestepper.Shop.RewardsFragment
import ca.unb.mobiledev.cookiestepper.databinding.ActivityStepTrackerBinding
class StepTracker : AppCompatActivity() {
    private lateinit var binding : ActivityStepTrackerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStepTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.stepTracker)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        replaceFragment(Steps())


        binding.bottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menuSteps -> replaceFragment(Steps())
                R.id.menuCalories -> replaceFragment(Calories())
                R.id.menuBMI -> replaceFragment(BMI())
                R.id.menuRewards -> replaceFragment(RewardsFragment())
                R.id.menuHistory -> replaceFragment(History())

                else -> {

                }

            }
            true
        }

        val settingButton = findViewById<ImageButton>(R.id.settingsButton)

        settingButton?.setOnClickListener {
            val settingsIntent = Intent(this@StepTracker, Settings::class.java)
            startActivity(settingsIntent)
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }
}