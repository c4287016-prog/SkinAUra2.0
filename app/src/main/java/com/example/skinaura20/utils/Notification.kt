package com.example.skinaura20.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.skinaura20.MainActivity
import com.example.skinaura20.R

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Skincare Time! ✨"
        val message = intent.getStringExtra("message") ?: "Time to pamper your skin."

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "skincare_reminders"

        // 1. Channel Creation (Oreo+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Skincare Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Reminders for morning and night routines"
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        // 2. Notification click karne par app khule
        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Build Notification with Max Priority
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification) // Ensure this icon exists
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Lock screen par dikhega
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}