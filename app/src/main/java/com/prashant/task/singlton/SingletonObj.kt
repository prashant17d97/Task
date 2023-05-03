package com.prashant.task.singlton

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import androidx.documentfile.provider.DocumentFile
import com.prashant.task.fragments.mediamodel.MediaModel
import kotlinx.parcelize.Parcelize
import java.io.File
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

fun details(context: Context, fileUri: Uri): MediaModel {
    val documentFile = DocumentFile.fromSingleUri(context, fileUri)
    val file = documentFile?.uri?.path?.let { File(it) }
    val lastModified = file?.lastModified()

    return MediaModel(
        uri = fileUri,
        fileName = documentFile?.name ?: "",
        fileSize = (documentFile?.length() ?: 0L).bytesToMb(),
        fileType = documentFile?.type ?: "",
        createdDate = lastModified?.milliSecondsToDate() ?: ""
    )

}

@Parcelize
enum class MediaQuery(val value: String) :Parcelable {
    Image("image/*"),
    Video("video/*")
}