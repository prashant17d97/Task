package com.prashant.task.fragments.mediamodel

import android.net.Uri
import android.os.Parcelable
import com.prashant.task.adapter.AbstractModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaModel(
    val uri: Uri,
    val fileType: String = "",
    val fileName: String = "",
    val fileSize: String = "",
    val createdDate: String = ""
) : AbstractModel(), Parcelable {
    val isVideo: Boolean
        get() = fileType.contains("video")
}