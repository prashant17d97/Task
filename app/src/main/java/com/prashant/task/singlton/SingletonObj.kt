package com.prashant.task.singlton

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Long.bytesToMb(): String {
    val mb = this.toDouble() / 1000000.0
    return "${String.format("%.2f", mb)} MB"
}

fun Long.milliSecondsToDate(): String {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
    return dateTime.format(formatter)
}


@Parcelize
enum class MediaQuery(val value: String) :Parcelable {
    Image("image/*"),
    Video("video/*")
}