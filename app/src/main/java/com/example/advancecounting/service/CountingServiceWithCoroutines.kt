package com.example.advancecounting.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class CountingServiceWithCoroutines : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private val handler = Handler()
    private var count = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            while (count <= 100) {
                delay(1000) // Wait for 1 second
                Log.v("check_status_coroutine"," $count")
                // Update count and send update to MainActivity UI
                count++
                sendCountUpdate()
            }
            stopSelf() // Stop the service when counting is done
        }

        return START_STICKY
    }

    private fun sendCountUpdate() {
        val intent = Intent("COUNT_UPDATED")
        intent.putExtra("count", count)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel all coroutines when the service is destroyed
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}