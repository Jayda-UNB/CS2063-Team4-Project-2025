package ca.unb.mobiledev.cookiestepper

import android.Manifest
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GoogleApiAvailability
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.cookiestepper.ui.StepViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.LocalRecordingClient
import com.google.android.gms.fitness.data.LocalDataSet
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.gms.fitness.request.LocalDataReadRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
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
    private lateinit var localRecordingClient: LocalRecordingClient


    private  val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if(isGranted){
            Log.i(TAG, "ACTIVITY_RECOGNITION permission granted.")
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
        val app = requireActivity().application as App
        val factory = StepViewModel.Factory(app.repository)
        stepViewModel = ViewModelProvider(this, factory)[StepViewModel::class.java]
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
        testButton = view.findViewById(R.id.test_calculation_button)

        //set up temp button to verify calculation logic
        testButton.setOnClickListener {
            //test profile for calculation (175cm, 70kg)
            stepViewModel.saveUserProfile(175f, 70f)

            //simulate 500 steps update
            stepViewModel.simulateStepsUpdate(500)
            Toast.makeText(requireContext(), "Simulated 500 steps. Check UI updates.", Toast.LENGTH_SHORT).show()
        }

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


        localRecordingClient = FitnessLocal.getLocalRecordingClient(requireContext())

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
            //permission already granted, subscribe immediately
            subscribe()
        }else{
            //request permission
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }


    }

    fun readData() {
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)
        val readRequest =
            LocalDataReadRequest.Builder()
                .aggregate(LocalDataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

        localRecordingClient.readData(readRequest).addOnSuccessListener { response ->
            for (dataSet in response.buckets.flatMap { it.dataSets }) {
                dumpDataSet(dataSet)
            }
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data", e)
            }
    }



    fun dumpDataSet(dataSet : LocalDataSet){
        Log.i(TAG,"Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints){
            Log.i(TAG,"Data point:")
            Log.i(TAG,"\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTime(TimeUnit.HOURS)}")
            Log.i(TAG,"\tEnd: ${dp.getEndTime(TimeUnit.HOURS)}")
            for(field in dp.dataType.fields){
                Log.i(TAG,"\tLocalField: ${field.name.toString()} Local: ${dp.getValue(field)}")
            }
        }
    }

    //@RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun subscribe() {
        //subscribe to steps data
        @Suppress("MissingPermission")
        localRecordingClient.subscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscriber!")
                Toast.makeText(requireContext(),
                    "Step Tracking Started!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem subscribing", e)
            }
    }
    fun unsubscribe() {
        val localRecordingClient = FitnessLocal.getLocalRecordingClient(requireContext())
        //unsubscribe from steps data
        localRecordingClient.unsubscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i(TAG,"Successfully unsubscribed!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem unsubscribing", e)
            }
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
    }

}
