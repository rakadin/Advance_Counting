package com.example.advancecounting.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.advancecounting.R
import com.example.advancecounting.activity.MainActivity
import java.util.*

class NotificationBuilder(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createSimpleNotification(
        title: String,
        content: String,
        iconResId: Int
    ) {
        val notificationBuilder = NotificationCompat.Builder(context, "default_channel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(iconResId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(getNotificationId(), notificationBuilder.build())
    }

    fun createNotificationWithSoundThatOpenTheApp(
        title: String,
        content: String,
        iconResId: Int
    ) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val notificationBuilder = NotificationCompat.Builder(context, "default_channel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(iconResId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        notificationManager.notify(getNotificationId(), notificationBuilder.build())
    }
    fun createNotificationWithSound(
        title: String,
        content: String,
        iconResId: Int
    ) {
        val notificationBuilder = NotificationCompat.Builder(context, "default_channel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(iconResId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        notificationManager.notify(getNotificationId(), notificationBuilder.build())
    }

    private fun getNotificationId(): Int {
        // Generate a unique notification ID here
        return 1
    }
}
