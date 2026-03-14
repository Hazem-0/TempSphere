package com.darkzoom.tempsphere.data.local.model


data class AlertModel(
    val id: Int,
    val timeText: String,
    val alertType: String,
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int,
    val repeatMode: RepeatMode = RepeatMode.DAILY
) {
    companion object {
        const val TYPE_NOTIFICATION = "Notification"
        const val TYPE_ALARM        = "Alarm Sound"
    }
}