package com.darkzoom.tempsphere.data.local.model


enum class RepeatMode {

    ONCE,

    DAILY;

    fun toStorageString(): String = name

    fun displayLabel(): String = when (this) {
        ONCE     -> "Once"
        DAILY    -> "Every day"

    }

    companion object {
        fun fromString(value: String): RepeatMode =
            entries.firstOrNull { it.name == value } ?: DAILY
        val all: List<RepeatMode> = entries.toList()
    }
}