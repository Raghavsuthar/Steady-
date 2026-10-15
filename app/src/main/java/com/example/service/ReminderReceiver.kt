package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val weekdaysOnly = intent.getBooleanExtra(EXTRA_WEEKDAYS_ONLY, false)
        val includeWeekends = intent.getBooleanExtra(EXTRA_INCLUDE_WEEKENDS, true)

        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)

        if (weekdaysOnly && isWeekend) {
            return
        }
        if (!includeWeekends && isWeekend) {
            return
        }

        showPracticeNotification(context)
    }

    private fun showPracticeNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Steady Practice",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle daily practice reminders to face uncertainty with acceptance"
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time for your daily Steady practice")
            .setContentText("Uncertainty is safe today. Take a few minutes for your practice.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Uncertainty is safe today. Take a few minutes for your practice without rituals.")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "steady_practice_channel"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_WEEKDAYS_ONLY = "extra_weekdays_only"
        const val EXTRA_INCLUDE_WEEKENDS = "extra_include_weekends"

        fun createReminderIntent(
            context: Context,
            weekdaysOnly: Boolean,
            includeWeekends: Boolean
        ): Intent {
            return Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_WEEKDAYS_ONLY, weekdaysOnly)
                putExtra(EXTRA_INCLUDE_WEEKENDS, includeWeekends)
            }
        }
    }
}
