package com.example.advancecounting.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.widget.Toast
import com.example.advancecounting.R
import com.example.advancecounting.notification.NotificationBuilder

class CountingService : Service() {
    private var stopCounting = false
    private var count = 0
    private var maxCount = 100
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var isBoundCheck = false
    private var isForeCheck = false
    private var isBackCheck = false
    private val binder = CountingBinder()
    // Binder for clients to communicate with the service
    inner class CountingBinder : Binder() {
        fun getService(): CountingService = this@CountingService
    }
    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Check if counting should be stopped
            if (stopCounting) {
                // Counting was stopped, stop the service
                stopSelf(msg.arg1)
                return
            }
            // increment the count value
            Log.v("check_status","update count before")
            count++
            Log.v("check_status","update count after")
            Log.v("check_status_but","check but: $isBoundCheck")
            Log.v("check_status_but","check but: $isForeCheck")
            Log.v("check_status_but","check but: $isBackCheck")


            if(isBoundCheck == true){ // use bound service
                Log.v("check_status","check but: $isBoundCheck")

                // send Broadcast to update the UI
                val intent = Intent("COUNT_UPDATED")
                intent.putExtra("count", count)
                sendBroadcast(intent)
            }
            else if(isForeCheck == true){ // use foreground service
                Log.v("check_status_but","check but: $isForeCheck")
                val notificationBuilder = NotificationBuilder(applicationContext)

                // Create a simple notification
                notificationBuilder.createSimpleNotification("Counting up by foreground service", "Now: $count", R.drawable.running)
                if(count==100){
                    val notificationBuilder = NotificationBuilder(applicationContext)

                    // Create a simple notification
                    notificationBuilder.createSimpleNotification("Foreground service complete", "$count Counting complete!!!", R.drawable.running)
                }
            }
            else if(isBackCheck == true){// use background service
                Log.v("check_status","check but: $isBackCheck")
                if(count==100){
                    val notificationBuilder = NotificationBuilder(applicationContext)

                    // Create a simple notification
                    notificationBuilder.createSimpleNotification("Background service complete", "$count Counting complete!!!", R.drawable.running)
                }
            }

            // Continue counting until reaching the maximum count
            if (count < maxCount) {
                // Send a delayed message to self to continue counting
                sendEmptyMessageDelayed(0, 1000) // Count every 1 second
            } else {
                // Counting completed, stop the service
                stopSelf(msg.arg1)
            }

        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.v("check_status","create service")

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceArgu", THREAD_PRIORITY_BACKGROUND).apply {
            start()
            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        Log.v("check_status","start command service")
        isBoundCheck = intent.getBooleanExtra("bound_called", false)
        isForeCheck = intent.getBooleanExtra("fore_called", false)
        isBackCheck = intent.getBooleanExtra("back_called", false)


        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)

        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        //("Return the communication channel to the service.")
        return binder // null if dont provide binding
    }


    override fun onDestroy() {
        stopCounting = true
        Log.v("check_status","destroy service = true")
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }
}