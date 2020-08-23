package com.khs.lovelynote.extension

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 날짜
 *
 * - yyyy/MM/dd aa HH:mm:ss
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 1:09
 **/
fun Date.toSimpleString(): String {
    val format = SimpleDateFormat("yyyy/MM/dd aa HH:mm:ss")
    return format.format(this)
}

/**
 * 시간
 *
 * - 00:00:00
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 1:10
 **/
fun Long.parseTime(): String {
    if (TimeUnit.MILLISECONDS.toHours(this) == 0L) {
        // append only min and hours
        val FORMAT = "%02d:%02d"
        return String.format(
            FORMAT, TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(this)
            ),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)
            )
        )
    } else {
        val FORMAT = "%02d:%02d:%02d"
        return String.format(
            FORMAT,
            TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(this)
            ),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)
            )
        )
    }
}

/**
 * 현재 시간
 *
 * - yyyyMMdd_HHmmss
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 1:10
 **/
fun currentTimeStamp(): String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
