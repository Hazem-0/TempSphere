package com.darkzoom.tempsphere.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.data.local.model.RepeatMode
import com.darkzoom.tempsphere.ui.receiver.AlertReceiver
import java.util.Calendar


class AlertManager(private val context: Context) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    fun canScheduleExactAlarms(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            alarmManager.canScheduleExactAlarms()
        else true


    fun scheduleAlert(alert: AlertModel) {
        if (!canScheduleExactAlarms()) return
        val triggerMillis = nextTriggerMillis(alert.hour, alert.minute, alert.repeatMode)
        setExactAlarm(alert, triggerMillis)
    }

    fun scheduleNextOccurrence(alert: AlertModel) {
        if (!canScheduleExactAlarms()) return
        val triggerMillis = nextTriggerMillis(
            alert.hour, alert.minute, alert.repeatMode, skipToday = true
        )
        setExactAlarm(alert, triggerMillis)
    }

    fun cancelAlert(alert: AlertModel) {
        alarmManager.cancel(buildPendingIntent(alert))
    }


    private fun nextTriggerMillis(
        hour: Int,
        minute: Int,
        repeatMode: RepeatMode,
        skipToday: Boolean = false
    ): Long {
        val now = Calendar.getInstance()

        val candidate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (skipToday || !candidate.after(now)) {
            candidate.add(Calendar.DATE, 1)
        }



        return candidate.timeInMillis
    }



    private fun setExactAlarm(alert: AlertModel, triggerAtMillis: Long) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            buildPendingIntent(alert)
        )
    }

    private fun buildPendingIntent(alert: AlertModel): PendingIntent {
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra(EXTRA_ALERT_ID,     alert.id)
            putExtra(EXTRA_ALERT_TYPE,   alert.alertType)
            putExtra(EXTRA_ALERT_HOUR,   alert.hour)
            putExtra(EXTRA_ALERT_MINUTE, alert.minute)
        }
        return PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val EXTRA_ALERT_ID     = "ALERT_ID"
        const val EXTRA_ALERT_TYPE   = "ALERT_TYPE"
        const val EXTRA_ALERT_HOUR   = "ALERT_HOUR"
        const val EXTRA_ALERT_MINUTE = "ALERT_MINUTE"
    }
}