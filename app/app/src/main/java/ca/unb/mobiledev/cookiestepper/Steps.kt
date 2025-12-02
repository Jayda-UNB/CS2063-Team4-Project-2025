package ca.unb.mobiledev.cookiestepper

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GoogleApiAvailability
import ca.unb.mobiledev.cookiestepper.Service.StepService
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.cookiestepper.ui.StepViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.fitness.LocalRecordingClient
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
@Suppress("DEPRECATION")
class Steps : Fragment() {

    //viewmodel and ui properties
    private lateinit var stepViewModel: StepViewModel
    private lateinit var stepsTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var dailyGoalTextView: TextView
    private lateinit var stepProgressBar: ProgressBar
    private var dailyGoal = 6000 //default step goal for now
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var currentSteps = 0

    private  val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if(isGranted){
            Log.i(TAG, "ACTIVITY_RECOGNITION permission granted.")
            startStepService()
        }
        else{
            Log.e(TAG, "ACTIVITY_RECOGNITION permission denied")
            Toast.makeText(requireContext(),
                "Permission denied. Step tracking will not work." ,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialize the viewModel using the Factory
        stepViewModel = ViewModelProvider(this, StepViewModel.Factory)[StepViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_steps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize UI elements
        stepsTextView = view.findViewById(R.id.current_steps_text)
        distanceTextView = view.findViewById(R.id.current_distance_text)
        caloriesTextView = view.findViewById(R.id.current_calorie_text)
        dailyGoalTextView = view.findViewById(R.id.daily_goal_text)
        stepProgressBar = view.findViewById(R.id.progressBar2)

        dailyGoalTextView.text = dailyGoal.toString()
        stepProgressBar.max = 100

        //observe live data
        stepViewModel.todayStepData.observe(viewLifecycleOwner, Observer{ stepData ->
            if (stepData != null) {
                //update ui with calculated data from the database
                currentSteps = stepData.steps
                stepsTextView.text = stepData.steps.toString()
                distanceTextView.text = String.format("%.2f km", stepData.distance)
                caloriesTextView.text = stepData.caloriesBurned.roundToInt().toString()
                updateProgressBar(currentSteps, dailyGoal)

                Log.d(
                    TAG,
                    "UI Updated! Steps: ${stepData.steps}, Distance: ${stepData.distance}, Calories: ${stepData.caloriesBurned}"
                )
            }
            else{
                stepsTextView.text = "0"
                distanceTextView.text = "0.00km"
                caloriesTextView.text = "0"
                updateProgressBar(0,dailyGoal)
            }
        })


        //check for minimum play services version
        val hasMinPlayServices = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext(),LocalRecordingClient.LOCAL_RECORDING_CLIENT_MIN_VERSION_CODE)
        if(hasMinPlayServices != ConnectionResult.SUCCESS){
            Toast.makeText(
                requireContext(),
                "Update Google Play services app for the app to work",
                Toast.LENGTH_SHORT
            ).show()
             return //exit if services aren't available
        }

        //request permission
        if (context?.let {
            androidx.core.content.ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            } == android.content.pm.PackageManager.PERMISSION_GRANTED){
            startStepService()
        }else{
            //request permission
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }


    }

    private fun updateProgressBar(steps: Int, goal: Int){
        if(goal<=0){
            stepProgressBar.progress = 0
            return
        }

        //calculate progress percentage
            val progressPercentage = ((steps.toFloat()/ goal.toFloat()) * 100).toInt()
            stepProgressBar.progress = if(progressPercentage >100) 100 else progressPercentage
        Log.d(TAG, "Progress calculated: $progressPercentage% (Steps: $steps / Goal: $goal)")
    }

    private fun startStepService(){
        val intent = Intent(requireContext(), StepService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        Log.i(TAG,"StepService requested to start.")
    }


    companion object{
            // String for LogCat documentation
            private const val TAG = "Steps Fragment"
    }
}
