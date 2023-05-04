package com.prashant.task.fragments.videos

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import com.prashant.task.R
import com.prashant.task.adapter.RecyclerAdapter
import com.prashant.task.fragments.mediamodel.MediaModel
import com.prashant.task.singlton.bytesToMb
import com.prashant.task.singlton.milliSecondsToDate
import java.io.File

class VideoVM :ViewModel() {

    val showFab = ObservableField(false)
    val isAdapterEmpty = ObservableField(false)
    val recycleAdapter = RecyclerAdapter<MediaModel>(R.layout.media_view)

    init {
        isAdapterEmpty.set(recycleAdapter.getAllItem().isEmpty())
    }

    fun updateAdapter(context: Context, uris: List<Uri>) {
        val list = arrayListOf<MediaModel>()
        uris.forEach {
            val dat = details(context, it)
            Log.e(TAG, "updateAdapter: $dat")
            list.add(dat)
        }
        recycleAdapter.addItems(list)
        isAdapterEmpty.set(recycleAdapter.getAllItem().isEmpty())
    }

    private fun details(context: Context, fileUri: Uri): MediaModel {
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
}
