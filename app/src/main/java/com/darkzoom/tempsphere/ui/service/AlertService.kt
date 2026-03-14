package com.darkzoom.tempsphere.ui.service

import CurrentWeatherEntity
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.darkzoom.tempsphere.ui.core.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AlertService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_ALARM) {
            stopAlarmAndSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        val stopPi = buildStopPendingIntent()
        startForeground(
            NOTIFICATION_ID,
            buildNotification(TITLE_LOADING, TEXT_LOADING, TEXT_LOADING, stopPi)
        )

        startAlarmAudio()
        fetchWeatherThenUpdateNotification(stopPi)

        return START_STICKY
    }


    private fun startAlarmAudio() {
        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(applicationContext, Settings.System.DEFAULT_ALARM_ALERT_URI)
                isLooping = true
                prepare()
                start()
            }
        }
    }

    private fun stopAlarmAndSelf() {
        runCatching { mediaPlayer?.stop(); mediaPlayer?.release() }
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    private fun fetchWeatherThenUpdateNotification(stopPi: PendingIntent) {
        serviceScope.launch {
            val (title, body, expanded) = buildWeatherStrings()
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_ID, buildNotification(title, body, expanded, stopPi))
        }
    }

    private suspend fun buildWeatherStrings(): Triple<String, String, String> {
        return try {
            val app        = applicationContext as App
            val units      = app.settingsRepository.tempUnit.toApiUnits()
            val lang       = app.settingsRepository.language.toApiLang()
            val unitSymbol = units.toUnitSymbol()

            val location = runCatching { app.locationTracker.getCurrentLocation() }.getOrNull()
            if (location != null) {
                runCatching {
                    app.repository.refreshCurrentWeather(
                        location.first, location.second, units, lang
                    )
                }
            }

            val entity = app.repository.getCurrentWeather(0.0, 0.0, units, lang)
                .firstOrNull()
                ?: return fallbackStrings()

            Triple(
                getString(com.darkzoom.tempsphere.R.string.alarm, entity.cityName),
                "${entity.temp.toInt()}$unitSymbol",
                buildExpandedText(entity, unitSymbol)
            )
        } catch (e: Exception) {
            fallbackStrings()
        }
    }

    private fun fallbackStrings() = Triple(
        getString(com.darkzoom.tempsphere.R.string.weather_alarm),
        getString(com.darkzoom.tempsphere.R.string.time_to_wake_up_open_tempsphere_for_the_forecast),
        getString(com.darkzoom.tempsphere.R.string.time_to_wake_up_open_tempsphere_for_the_forecast),
    )


    private fun buildStopPendingIntent() = PendingIntent.getService(
        this,
        REQUEST_STOP,
        Intent(this, AlertService::class.java).apply { action = ACTION_STOP_ALARM },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun buildNotification(
        title: String,
        text: String,
        expandedText: String,
        stopPi: PendingIntent
    ): Notification {

        val fullScreenIntent = Intent().apply {
            // Replace "MainActivity" if your main entry point has a different name
            setClassName(this@AlertService, "com.darkzoom.tempsphere.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val fullScreenPi = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .addAction(R.drawable.ic_menu_close_clear_cancel, "Stop Alarm", stopPi)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(fullScreenPi)
            .setFullScreenIntent(fullScreenPi, true) // Wakes the screen
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID, "Alarm Service", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setBypassDnd(true)
                enableVibration(true)
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }


    override fun onDestroy() {
        runCatching { mediaPlayer?.stop(); mediaPlayer?.release() }
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_ALERT_ID    = "ALERT_ID"
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"

        private const val CHANNEL_ID      = "ALARM_SERVICE_CHANNEL"
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_STOP    = 1
        private const val TITLE_LOADING   = "⏰ Weather Alarm"
        private const val TEXT_LOADING    = "Fetching current weather…"
    }
}


private fun String.toApiUnits()    = when (this) { "Celsius" -> "metric"; "Fahrenheit" -> "imperial"; else -> "standard" }
private fun String.toApiLang()     = when (this) { "Arabic"  -> "ar";     else -> "en" }
private fun String.toUnitSymbol()  = when (this) { "metric"  -> "°C";     "imperial" -> "°F";         else -> " K" }

private fun buildExpandedText(entity: CurrentWeatherEntity, unitSymbol: String): String =
    "${entity.temp.toInt()}$unitSymbol\n" +
            "Feels like ${entity.feelsLike.toInt()}$unitSymbol\n" +
            "↑ ${entity.tempMax.toInt()}$unitSymbol  ↓ ${entity.tempMin.toInt()}$unitSymbol · " +
            "💧 ${entity.humidity}%  💨 ${entity.windSpeed} m/s"