package ca.unb.mobiledev.cookiestepper

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GoogleApiAvailability
import ca.unb.mobiledev.cookiestepper.Service.StepService
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ca.unb.mobiledev.cookiestepper.ui.StepViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.fitness.LocalRecordingClient
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Steps.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class Steps : Fragment() {

    //viewmodel and ui properties
    private lateinit var stepViewModel: StepViewModel
    private lateinit var stepsTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var testButton: Button //temp button for testing

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var stepCount: Int = 0;



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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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

        //observe live data
        stepViewModel.todayStepData.observe(viewLifecycleOwner, Observer{ stepData ->
            if (stepData != null) {
                //update ui with calculated data from the database
                stepsTextView.text = stepData.steps.toString()
                distanceTextView.text = String.format("%.2f km", stepData.distance)
                caloriesTextView.text = stepData.caloriesBurned.roundToInt().toString()

                Log.d(
                    TAG,
                    "UI Updated! Steps: ${stepData.steps}, Distance: ${stepData.distance}, Calories: ${stepData.caloriesBurned}"
                )
            }
            else{
                stepsTextView.text = "0"
                distanceTextView.text = "0.00km"
                caloriesTextView.text = "0"
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


    private fun startStepService(){
        val intent = Intent(requireContext(), StepService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        Log.i(TAG,"StepService requested to start.")
    }


    companion object{
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Steps.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                Steps().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

            // String for LogCat documentation
            private const val TAG = "Steps Fragment"

    }

}
