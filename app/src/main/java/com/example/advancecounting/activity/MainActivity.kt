package com.example.advancecounting.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.advancecounting.R
import com.example.advancecounting.notification.NotificationAndServiceWorker
import com.example.advancecounting.notification.NotificationWorker
import com.example.advancecounting.service.BoundCountingService
import com.example.advancecounting.service.CountingService
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private var boundCountingService: BoundCountingService? = null
    private lateinit var boundModeButton: TextView
    private lateinit var foreModeButton: TextView
    private lateinit var backModeButton: TextView
    private lateinit var count_value_text : TextView
    private lateinit var serviceIntent : Intent
    private lateinit var boundSVintent : Intent

    private var isUsingBound = false
    private var isUsingForeGround = false
    private var isUsingBackground = false
    private var isBound = false

    private val countUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "COUNT_UPDATED") {
                Log.v("check_status","get count update")

                val count = intent.getIntExtra("count", 0)
                // Update UI with the count value
                updateUI(count)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        serviceIntent = Intent(this, CountingService::class.java)
        boundSVintent = Intent(this, BoundCountingService::class.java)
        getIDs()

        // Schedule the initial notification work request
        scheduleInitialNotification()
        scheduleNotificationAndService()
    }
    private fun scheduleInitialNotification() {
        // Get the current time
        val currentTime = Calendar.getInstance()

        // Create a target time for the notification (15:00 or 3:00 PM)
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15) // Set the hour to 15 (3:00 PM)
            set(Calendar.MINUTE, 22)       // Set the minute to 0
            set(Calendar.SECOND, 0)       // Set the second to 0
        }

        // Check if the current time is after the target time for today
        if (currentTime.after(targetTime)) {
            // If yes, schedule the notification for the next day at the same time
            targetTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Calculate the delay until the target time
        val delay = targetTime.timeInMillis - currentTime.timeInMillis

        // Create a one-time work request to show the initial notification
        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        // Enqueue the work request to WorkManager for scheduling
        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }
    private fun scheduleNotificationAndService() {
        val notificationAndServiceWorkRequest = OneTimeWorkRequestBuilder<NotificationAndServiceWorker>()
            .build()

        WorkManager.getInstance(this).enqueue(notificationAndServiceWorkRequest)
    }
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the count update receiver
        unregisterReceiver(countUpdateReceiver)
        Log.v("check_status","Destroy MainActivity")

    }
    fun getIDs(){
        boundModeButton = findViewById(R.id.bound_service_mode)
        foreModeButton = findViewById(R.id.foreground_service_mode)
        backModeButton = findViewById(R.id.background_service_mode)
        count_value_text = findViewById(R.id.total_count_in)
    }
    fun StartCountingFunc(view: View) {
        Log.v("check_status","start counting")
        // Register the count update receiver
        Log.v("check_status_but","check before but: $isUsingForeGround")
        Log.v("check_status_but","check before but: $isUsingBackground")
        Log.v("check_status_but","check before but: $isUsingBound")

        registerReceiver(countUpdateReceiver, IntentFilter("COUNT_UPDATED"))

        // send button ch

        if(isUsingBound == true){ // start binding
            bindService()
        }
        else{ // another service
            // Create and start the service
            serviceIntent.putExtra("fore_called", isUsingForeGround)
            serviceIntent.putExtra("back_called", isUsingBackground)
            Log.v("check_status","send fore: $isUsingForeGround")
            Log.v("check_status","send back: $isUsingBackground")

            startService(serviceIntent)
        }


    }
    // binding servicce
    fun bindService() {
        bindService(boundSVintent, connection, BIND_AUTO_CREATE)
    }

    fun unbindService(view :View) {
        if (isBound == true) {
            Log.v("check_status","stop service binding 2")
            unbindService(connection)
            stopService(boundSVintent)
            isBound = false
        }
    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BoundCountingService.CountingBinder
            boundCountingService = binder.getService()
            isBound = true
            updateUI(boundCountingService?.getCount() ?: 0)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }
    // end binding service
    fun StopCountingFunc(view: View) {
        Log.v("check_status","stop service")
        if(isUsingBound == true){
            Log.v("check_status","stop service binding")
            unbindService(view)
        }
        else{
            stopService(serviceIntent)
        }
    }

    fun useBoundServiceFunc(view: View) {
        setBackGroundClickButton(boundModeButton,foreModeButton,backModeButton)
        isUsingBound = true
        isUsingBackground = false
        isUsingForeGround = false
    }
    fun useForegroundServiceFunc(view: View) {
        setBackGroundClickButton(foreModeButton,boundModeButton,backModeButton)
        isUsingBound = false
        isUsingBackground = false
        isUsingForeGround = true
    }
    fun useBackgroundServiceFunc(view: View) {
        setBackGroundClickButton(backModeButton ,foreModeButton,boundModeButton)
        isUsingBound = false
        isUsingBackground = true
        isUsingForeGround = false
    }
    fun setBackGroundClickButton(clickedBut : TextView, otherBut : TextView,otherBut2 : TextView){
        clickedBut.setBackgroundResource(R.drawable.gradient_negative_button)
        otherBut.setBackgroundResource(R.drawable.gradient_possitive_button)
        otherBut2.setBackgroundResource(R.drawable.gradient_possitive_button)

    }
    private fun updateUI(count: Int) {
        // Update your UI elements with the received count value
        count_value_text.text = "$count"
    }

    override fun onStop() {
        super.onStop()
        Log.v("check_status_activity","stop MainActivity")
    }

    override fun onResume() {
        super.onResume()
        Log.v("check_status_activity","resume MainActivity")
    }

    override fun onStart() {
        super.onStart()
        Log.v("check_status_activity","start MainActivity")
    }
}