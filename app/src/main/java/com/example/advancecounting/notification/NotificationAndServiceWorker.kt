package com.example.advancecounting.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import android.content.Intent
import android.util.Log
import com.example.advancecounting.service.CountingService

class NotificationAndServiceWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        Log.v("check_status_noti_schedule","do work triggered")
        scheduleNotificationAndService()
        return Result.success()
    }

    private fun scheduleNotificationAndService() {
        Log.v("check_status_noti_schedule","triggered")
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16) // 16:00 (4:00 PM)
            set(Calendar.MINUTE, 47)
            set(Calendar.SECOND, 0)
        }
        val targetTime2 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16) // 16:00 (4:00 PM)
            set(Calendar.MINUTE, 50)
            set(Calendar.SECOND, 0)
        }
        // If the target time has already passed for today, schedule it for the next day
        if (currentTime.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1)
        }
        /*
        The delay calculation ensures that the worker will wait for the
        calculated delay duration after the current time.
        It won't necessarily trigger at exactly 16:31, as there
        might be some minor variations due to system factors or other
         app activities.
         */
        val delay = targetTime.timeInMillis - currentTime.timeInMillis
        val delay2 = targetTime2.timeInMillis - currentTime.timeInMillis

        // Create an intent to start the CountingService
        val serviceIntent = Intent(applicationContext, CountingService::class.java)

        if(currentTime == targetTime){
            serviceIntent.putExtra("back_called", true) // Add any necessary data
        }
        if(currentTime == targetTime2){
            serviceIntent.putExtra("fore_called", true) // Add any necessary data
        }

        // Schedule a one-time work request to show the notification and start the service
        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationAndServiceWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(notificationWorkRequest)
        applicationContext.startService(serviceIntent)
    }
}
