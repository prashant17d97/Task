package com.prashant.task.singlton

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.window.layout.WindowMetricsCalculator
import com.prashant.task.R
import com.prashant.task.adapter.RecyclerAdapter
import com.prashant.task.databinding.CameraFragmentBinding
import com.prashant.task.databinding.PreviewBinding
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


@Parcelize
enum class MediaQuery(val value: String) : Parcelable {
    Image("image/*"),
    Video("video/*")
}

fun showCustomDialog(
    activity: Activity,
    binding: (CameraFragmentBinding, AlertDialog) -> Unit
) {
    val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
    val currentBounds = windowMetrics.bounds
    val width = currentBounds.width()
    val height = currentBounds.height()
    val builder = AlertDialog.Builder(activity, R.style.TransparentDialog)
    val dialogBinding = CameraFragmentBinding.inflate(LayoutInflater.from(activity))
    dialogBinding.clCamera.minWidth = (width * 0.8).toInt()
    dialogBinding.clCamera.minHeight = (height * 0.8).toInt()
    builder.setView(dialogBinding.root)

    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    binding(dialogBinding, alertDialog)
}

fun showPreviewDialog(
    activity: Activity,
    mediaList: ArrayList<MediaModel>,
    position: Int
) {
    val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
    val currentBounds = windowMetrics.bounds
    val width = currentBounds.width()
    val height = currentBounds.height()
    val builder = AlertDialog.Builder(activity, R.style.TransparentDialog)

    val dialogBinding = PreviewBinding.inflate(LayoutInflater.from(activity))
    dialogBinding.clPreview.minWidth = (width * 0.85).toInt()
    dialogBinding.clPreview.minHeight = (height * 0.85).toInt()
    val adapter = RecyclerAdapter<MediaModel>(R.layout.media_preview)
    dialogBinding.rvPreview.adapter = adapter
    adapter.addItems(mediaList)
    dialogBinding.rvPreview.scrollToPosition(position)

    builder.setView(dialogBinding.root)
    val alertDialog = builder.create()
    dialogBinding.ivCancelPreview.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    alertDialog.show()
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
