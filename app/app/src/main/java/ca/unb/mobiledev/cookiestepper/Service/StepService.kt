@file:Suppress("DEPRECATION")

package ca.unb.mobiledev.cookiestepper.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import ca.unb.mobiledev.cookiestepper.App
import ca.unb.mobiledev.cookiestepper.ui.StepViewModel
import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import ca.unb.mobiledev.cookiestepper.Constants
import ca.unb.mobiledev.cookiestepper.R
import ca.unb.mobiledev.cookiestepper.MainActivity
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.LocalRecordingClient
import com.google.android.gms.fitness.data.LocalDataSet
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.gms.fitness.data.LocalField
import com.google.android.gms.fitness.request.LocalDataReadRequest
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ca.unb.mobiledev.cookiestepper.repositories.StepRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper


//Foreground service responsible for continuous, background step tracking

@Suppress("DEPRECATION")
class StepService : Service() {

    private lateinit var stepViewModel: StepViewModel
    private lateinit var stepRepository: StepRepository
    private val NOTIFICATION_CHANNEL_ID = "step_tracker_channel"
    private val NOTIFICATION_ID = 101
    private lateinit var localRecordingClient: LocalRecordingClient


    // Polling mechanism
    private val handler = Handler(Looper.getMainLooper())
    private val READ_DELAY_MS = 5000L // Read steps every 5 seconds


    private val stepReadingRunnable = object : Runnable{
        override fun run(){
            readData()
            handler.postDelayed(this,READ_DELAY_MS)
        }
    }
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        val notification = createNotification()
        //start service as a foreground service
        startForeground(NOTIFICATION_ID, notification)

        localRecordingClient = FitnessLocal.getLocalRecordingClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //permission denied
            Log.e("StepService", "ACTIVITY_RECOGNITION permission not granted.")
            return
        }

        //initialize viewModel
        val app = application as App
        stepViewModel = StepViewModel(app.repository)
        subscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
        handler.post(stepReadingRunnable)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("StepService", "Service started/restarted with START_STICKY.")
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        handler.removeCallbacks(stepReadingRunnable)
        unsubscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
        super.onDestroy()
    }

    private fun getLocalField(localDataType: LocalDataType): LocalField{
        return when (localDataType) {
            LocalDataType.TYPE_STEP_COUNT_DELTA -> LocalField.FIELD_STEPS
            else -> throw IllegalArgumentException("Unsupported LocalDataType: $localDataType")
        }
    }
    //@RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun subscribe(localDataType: LocalDataType) {
        //subscribe to steps data
        @Suppress("MissingPermission")
        localRecordingClient.subscribe(localDataType)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscriber!")
                Toast.makeText(
                    this,
                    "Step Tracking Started!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem subscribing", e)
            }
    }

    fun unsubscribe(localDataType: LocalDataType) {

        //unsubscribe from steps data
        localRecordingClient.unsubscribe(localDataType)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully unsubscribed!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem unsubscribing", e)
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //set channel name
            val name = "Cookie Stepper Background Service"
            //set channel description
            val description = "Notifies that step tracking is active"
            // Set the channel importance
            val importance = NotificationManager.IMPORTANCE_LOW

            //create the NotificationChannel object
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = description
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            channel.enableLights(true)
            channel.enableVibration(false)

            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            channel.lightColor = Color.GREEN

            //register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Cookie Stepper Active")
            .setContentText("Tracking steps.....")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun readData() {
        Log.i(TAG, "Executing readData() to fetch steps...")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis //start of today
        val endTime = System.currentTimeMillis() //now
        val dataType = LocalDataType.TYPE_STEP_COUNT_DELTA
        val localField = getLocalField(dataType)

        val readRequest =
            LocalDataReadRequest.Builder()
                .aggregate(dataType)
                .bucketByTime(1, TimeUnit.MINUTES)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()

        localRecordingClient.readData(readRequest).addOnSuccessListener { response ->
            var totalStepsToday = 0
            Log.i(TAG, "Read Success: Received ${response.buckets.size} data buckets.")
            for (dataSet in response.buckets.flatMap { it.dataSets }) {
                Log.i(TAG, "Processing data set: ${dataSet.dataType.name}")
                for (dp in dataSet.dataPoints) {
                    if (dp.dataType.fields.contains(localField)) {
                        val steps = dp.getValue(localField).asInt()
                        totalStepsToday += steps
                        Log.d(TAG, "Found step data point: $steps")

                    }
                }
            }

            Log.i(TAG, "Total steps calculated for today: $totalStepsToday")

            if (totalStepsToday >=0) {
                //update local repository
                stepViewModel.setAbsoluteDailySteps(totalStepsToday)
                Log.d(TAG, "Step data sent to ViewModel for processing: $totalStepsToday")
            }
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data", e)
            }
    }

/*
    fun processAndSaveData(dataSet: LocalDataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")

        val expectedField = LocalField.FIELD_STEPS
        for (dp in dataSet.dataPoints) {
            for (field in dp.dataType.fields) {
                if (field == expectedField) {
                    val steps = dp.getValue(field).asInt()
                    Log.i(TAG, "Processing Step Value: $steps")
                    if(steps > 0) {
                        //update local repository
                        stepViewModel.recordNewSteps(steps)
                        Log.i(TAG, "Recorded steps: $steps")

                        val updateIntent = Intent(Constants.ACTION_STEPS_UPDATE).apply{
                            putExtra(Constants.EXTRA_STEPS_COUNT,steps)
                        }
                        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent)
                    }else{
                        Log.i(TAG, "Steps were 0, not saving.")
                    }
                }
            }
        }
    }*/


    companion object {
        // String for LogCat documentation
        private const val TAG = "StepService"
    }
}