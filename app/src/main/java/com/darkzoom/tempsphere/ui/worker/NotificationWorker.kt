package com.darkzoom.tempsphere.ui.worker

import android.Manifest
//noinspection SuspiciousImport
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darkzoom.tempsphere.data.local.model.entity.CurrentWeatherEntity
import com.darkzoom.tempsphere.ui.core.App
import com.darkzoom.tempsphere.utils.toApiLang
import com.darkzoom.tempsphere.utils.toApiUnits
import com.darkzoom.tempsphere.utils.toUnitSymbol
import kotlinx.coroutines.flow.firstOrNull

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        createNotificationChannel()


        val alertId = inputData.getInt(KEY_ALERT_ID, DEFAULT_NOTIF_ID)
        val (title, body, expanded) = buildWeatherStrings(context)
        showNotification(alertId, title, body, expanded)

        return Result.success()
    }


    private suspend fun buildWeatherStrings(context: Context): Triple<String, String, String> {
        return try {
            val app        = context.applicationContext as App
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
                context.getString(com.darkzoom.tempsphere.R.string.tempsphere, entity.cityName),
                "${entity.temp.toInt()}$unitSymbol }",
                buildExpandedText(context,entity, unitSymbol )
            )
        } catch (e: Exception) {
            fallbackStrings()
        }
    }

    private fun fallbackStrings() = Triple(
        context.getString(com.darkzoom.tempsphere.R.string.tempsphere_weather_alert),
        context.getString(com.darkzoom.tempsphere.R.string.check_the_latest_weather_conditions),
        context.getString(com.darkzoom.tempsphere.R.string.open_tempsphere_to_see_your_current_forecast)
    )


    private fun showNotification(id: Int, title: String, text: String, expandedText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notif)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH
            )
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    companion object {
        const val KEY_ALERT_ID         = "ALERT_ID"
        private const val CHANNEL_ID       = "WEATHER_ALERT_CHANNEL"
        private const val DEFAULT_NOTIF_ID = 2000
    }
}


private fun buildExpandedText(
    context: Context,
    entity: CurrentWeatherEntity,
    unitSymbol: String
): String =
    "${entity.temp.toInt()}$unitSymbol " +
            context.getString(
                com.darkzoom.tempsphere.R.string.feels_like,
                entity.feelsLike.toInt(),
                unitSymbol
            ) +
            " ↑ ${entity.tempMax.toInt()}$unitSymbol  ↓ ${entity.tempMin.toInt()}$unitSymbol · " +
            "💧 ${entity.humidity}%  💨 ${entity.windSpeed} m/s"