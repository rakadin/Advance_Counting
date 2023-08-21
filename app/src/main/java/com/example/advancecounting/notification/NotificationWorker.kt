package com.example.advancecounting.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.advancecounting.R
import com.example.advancecounting.activity.MainActivity

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    // This function is executed when the work request is triggered
    override fun doWork(): Result {
        // Show the notification
        showNotification()

        // Indicate that the work is successful
        return Result.success()
    }

    // Function to create and show the notification
    private fun showNotification() {
        val context = applicationContext

        // Obtain the notification service
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel if it doesn't exist
        val notificationChannel = NotificationChannel(
            "default_channel",
            "Default Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        // Create an Intent to open the MainActivity when the notification is clicked
        val notificationIntent = Intent(context, MainActivity::class.java)
        // Add a flag to ensure only one instance of MainActivity is open
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Create a PendingIntent that wraps the notification intent
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification using NotificationCompat
        val notificationBuilder = NotificationCompat.Builder(context, "default_channel")
            .setContentTitle("Scheduled Notification")
            .setContentText("Click to open MainActivity")
            .setSmallIcon(R.drawable.running)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Automatically dismiss the notification when clicked
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        // Display the notification using the notification manager
        notificationManager.notify(getNotificationId(), notificationBuilder.build())
    }

    // Function to generate a unique notification ID
    private fun getNotificationId(): Int {
        return 234 // You can generate a unique ID here
    }
}
