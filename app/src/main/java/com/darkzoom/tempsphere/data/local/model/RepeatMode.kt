package com.darkzoom.tempsphere.data.local.model

import android.content.Context
import com.darkzoom.tempsphere.R


enum class RepeatMode {

    ONCE,

    DAILY;

    fun toStorageString(): String = name

    fun displayLabel(context: Context): String = when (this) {
        ONCE     -> context.getString(R.string.once)
        DAILY    -> context.getString(R.string.every_day)

    }

    companion object {
        fun fromString(value: String): RepeatMode =
            entries.firstOrNull { it.name == value } ?: DAILY
        val all: List<RepeatMode> = entries.toList()
    }
}