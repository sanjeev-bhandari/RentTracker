package com.realsanjeev.renttracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.realsanjeev.renttracker.ui.dashboard.DashboardViewModel
import com.realsanjeev.renttracker.ui.navigation.RentTrackerNavHost
import com.realsanjeev.renttracker.ui.theme.RentTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isSystemDark = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        isDarkMode = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> isSystemDark
        }
        createNotificationChannel()

        setContent {
            RentTrackerTheme(darkTheme = isDarkMode) {
                RentTrackerNavHost(
                    dashboardViewModel = dashboardViewModel,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        isDarkMode = !isDarkMode
                        AppCompatDelegate.setDefaultNightMode(
                            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO
                        )
                    },
                    onToggleLanguage = {
                        val currentLang = resources.configuration.locales[0].language
                        val target = if (currentLang == "ne") "en" else "ne"
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(target)
                        )
                    },
                    onClearAllData = {
                        dashboardViewModel.clearAllData()
                        Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show()
                    },
                    onSendReminder = {
                        sendReminderNotification()
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Rent Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for rent due dates"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendReminderNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            return
        }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Rent Reminder")
                .setContentText("Pending tenants need attention. Tap to view details.")
                .setPriority(android.app.Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            android.app.Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Rent Reminder")
                .setContentText("Pending tenants need attention. Tap to view details.")
                .setPriority(android.app.Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
        }
        manager.notify(NOTIFICATION_ID, notification)
        Toast.makeText(this, "Reminder notification sent!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "rent_reminder_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
