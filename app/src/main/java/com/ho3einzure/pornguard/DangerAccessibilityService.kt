package com.ho3einzure.pornguard

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat

class DangerAccessibilityService : AccessibilityService() {

    private val keywords = listOf(
        "porn", "sex", "xxx", "nsfw",
        "پورن", "سکس", "شهوانی", "سوporno",
        "xvideos", "pornhub", "xnxx"
    )

    private val channelId = "danger_alerts"

    override fun onServiceConnected() {
        createNotificationChannel()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val sourceText = buildString {
            event.text?.forEach { append(it).append(' ') }
            event.contentDescription?.let { append(it).append(' ') }
        }.lowercase()

        val pkg = event.packageName?.toString()?.lowercase().orEmpty()
        val isBrowser = pkg.contains("chrome") || pkg.contains("browser") ||
                pkg.contains("firefox") || pkg.contains("opera") || pkg.contains("samsung")

        val hit = keywords.any { k -> sourceText.contains(k) }

        if (isBrowser && hit) {
            notifyDanger()
        }
    }

    override fun onInterrupt() {
        // no-op
    }

    private fun notifyDanger() {
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(getString(R.string.alert_title))
            .setContentText(getString(R.string.alert_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        mgr.notify(1001, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            mgr.createNotificationChannel(channel)
        }
    }
}
