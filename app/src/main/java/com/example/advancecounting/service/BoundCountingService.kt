package com.example.advancecounting.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.widget.Toast
import java.lang.ref.WeakReference

class BoundCountingService : Service() {
    private val binder = CountingBinder()
    private lateinit var countHandler: CountHandler
    private var count = 0
    private var stopCounting = false

    inner class CountingBinder : Binder() {
        fun getService(): BoundCountingService = this@BoundCountingService
    }

    override fun onCreate() {
        super.onCreate()
        countHandler = CountHandler(this)
        countHandler.sendEmptyMessage(0) // Start counting
    }
    override fun onBind(intent: Intent): IBinder? {
        Log.v("check_status","bindservice = $stopCounting")
        return binder
    }

    fun getCount(): Int {
        return count
    }

    private class CountHandler(service: BoundCountingService) : Handler() {
        private val serviceReference: WeakReference<BoundCountingService> = WeakReference(service)

        override fun handleMessage(msg: Message) {
            val service = serviceReference.get() // Get a reference to the BoundCountingService
            service?.let { // If the service is not null
                if(!it.stopCounting){
                    it.count++ // Increment the count value of the service
                    if (it.count <= 100) { // Check if the count is still within the desired range
                        it.countHandler.sendEmptyMessageDelayed(0, 1000) // Delay sending a message to itself after 1 second

                        // Notify the activity about the count update
                        val intent = Intent("COUNT_UPDATED") // Create an intent with action "COUNT_UPDATED"
                        intent.putExtra("count", it.count) // Add the current count value to the intent
                        it.sendBroadcast(intent) // Send the broadcast intent
                    }
                }

            }
        }

    }
    override fun onDestroy() {
        stopCounting = true
        Log.v("check_status","destroy service = true")
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }
}
