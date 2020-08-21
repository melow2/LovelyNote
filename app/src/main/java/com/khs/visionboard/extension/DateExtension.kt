package com.khs.visionboard.extension

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.toSimpleString() : String {
    val format = SimpleDateFormat("yyyy/MM/dd aa HH:mm:ss")
    return format.format(this)
}

fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
    SimpleDateFormat("dd.MM.yyyy").let { formatter ->
        formatter.parse("$day.$month.$year")?.time ?: 0
    }

fun Long.parseTime(): String {
    if (TimeUnit.MILLISECONDS.toHours(this) == 0L) {
        // append only min and hours
        val FORMAT = "%02d:%02d"
        return String.format(FORMAT, TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(this)),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)))
    } else {
        val FORMAT = "%02d:%02d:%02d"
        return String.format(FORMAT,
            TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(this)),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)))
    }
}

fun currentTimeStamp():String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
