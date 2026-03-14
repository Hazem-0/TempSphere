package com.darkzoom.tempsphere.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.ui.service.AlertService
import com.darkzoom.tempsphere.ui.core.App
import com.darkzoom.tempsphere.utils.AlertManager
import com.darkzoom.tempsphere.ui.worker.NotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlertReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.LOCKED_BOOT_COMPLETED" -> handleBoot(context)
            else -> handleAlarmFired(context, intent)
        }
    }

    private fun handleBoot(context: Context) {
        val app = context.applicationContext as App

        CoroutineScope(Dispatchers.IO).launch {
            app.alertRepository.rescheduleAllEnabled()
        }
    }


    private fun handleAlarmFired(context: Context, intent: Intent) {
        val alertId     = intent.getIntExtra(AlertManager.EXTRA_ALERT_ID,     -1)
        val alertType   = intent.getStringExtra(AlertManager.EXTRA_ALERT_TYPE) ?: return
        val alertHour   = intent.getIntExtra(AlertManager.EXTRA_ALERT_HOUR,   -1)
        val alertMinute = intent.getIntExtra(AlertManager.EXTRA_ALERT_MINUTE, -1)

        if (alertId == -1 || alertHour == -1 || alertMinute == -1) return

        dispatchAlert(context, alertId, alertType)

        val app = context.applicationContext as App
        CoroutineScope(Dispatchers.IO).launch {
            app.alertRepository.handleAlertFired(alertId)
        }
    }

    private fun dispatchAlert(context: Context, alertId: Int, alertType: String) {
        when (alertType) {
            AlertModel.TYPE_NOTIFICATION -> {
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(workDataOf(NotificationWorker.KEY_ALERT_ID to alertId))
                    .build()
                WorkManager.getInstance(context).enqueue(workRequest)
            }
            AlertModel.TYPE_ALARM -> {
                val serviceIntent = Intent(context, AlertService::class.java).apply {
                    putExtra(AlertService.EXTRA_ALERT_ID, alertId)
                }
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}