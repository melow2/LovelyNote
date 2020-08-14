package com.khs.visionboard.extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.toSimpleString() : String {
    val format = SimpleDateFormat("yyyy/MM/dd aa HH:mm:ss")
    return format.format(this)
}

fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
    SimpleDateFormat("dd.MM.yyyy").let { formatter ->
        formatter.parse("$day.$month.$year")?.time ?: 0
    }

fun Long.formateMilliSeccond(): String? {
    val milliseconds = this
    var finalTimeHour = ""
    var secondsString = ""
    var minuteString = ""

    // Convert total duration into time
    val hours = (milliseconds / (1000 * 60 * 60)).toInt()
    val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
    val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

    // Add hours if there
    if (hours > 0) {
        finalTimeHour = "0$hours:"
    }

    // Prepending 0 to seconds if it is one digit
    secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        "" + seconds
    }

    minuteString = if (minutes < 10) {
        "0$minutes:"
    } else {
        "$minutes:"
    }

    val formatResult = "$finalTimeHour$minuteString$secondsString"

    //      return  String.format("%02d Min, %02d Sec",
    //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
    //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
    //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

    // return timer string
    return formatResult
}