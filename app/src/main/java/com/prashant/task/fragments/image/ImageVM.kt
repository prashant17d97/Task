package com.prashant.task.fragments.image

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.prashant.task.R
import com.prashant.task.adapter.RecyclerAdapter
import com.prashant.task.fragments.mediamodel.MediaModel
import com.prashant.task.singlton.details

class ImageVM :ViewModel() {


    val showFab = ObservableField(false)
    val isAdapterEmpty = ObservableField(false)
    val recycleAdapter = RecyclerAdapter<MediaModel>(R.layout.media_view)

    init {
        isAdapterEmpty.set(recycleAdapter.getAllItem().isEmpty())
    }

    fun updateAdapter(context: Context, uris: List<Uri>) {
        val list = arrayListOf<MediaModel>()
        uris.forEach {
            list.add(details(context, it))
        }
        recycleAdapter.addItems(list)
        isAdapterEmpty.set(recycleAdapter.getAllItem().isEmpty())
    }
}
