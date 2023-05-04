package com.prashant.task.fragments.image

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prashant.task.MainActivity
import com.prashant.task.R
import com.prashant.task.adapter.RecyclerAdapter
import com.prashant.task.fragments.mediamodel.MediaModel
import com.prashant.task.singlton.SingletonObj.details
import com.prashant.task.singlton.SingletonObj.showPreviewDialog

class ImageVM :ViewModel() {

    var isCaptured: Boolean = false
    val showFab = ObservableField(false)
    val isAdapterEmpty = ObservableField(false)
    val recycleAdapter = RecyclerAdapter<MediaModel>(R.layout.media_view)
    val currentImage = MutableLiveData<Uri>(null)

    val previewAdapter = RecyclerAdapter<MediaModel>(R.layout.preview_view)


    init {
        isAdapterEmpty.set(recycleAdapter.getAllItem().isEmpty())
        previewAdapter.setOnItemClick { view, _, position ->
            showFab.set(false)
            when (view.id) {
                R.id.clPreview -> {
                    showFab.set(false)
                    currentImage.postValue(previewAdapter.getItemAt(position).uri)
                }
            }
        }
        recycleAdapter.setOnItemClick { view, _, position ->
            when (view.id) {
                R.id.clMediaCard -> {
                    showFab.set(false)
                    recycleAdapter.getItemAt(position).uri.let {
                        showPreviewDialog(
                            (MainActivity.activity.get() as MainActivity),
                            recycleAdapter.getAllItem(),
                            position
                        )
                    }
                }
            }
        }
    }

    fun updateAdapter(context: Context, uris: List<Uri>, done: (Int) -> Unit) {
        val list = arrayListOf<MediaModel>()
        uris.forEach {
            list.add(details(context, it))
        }
        if (recycleAdapter.getAllItem().isEmpty()) {
            recycleAdapter.addItems(list)
            isAdapterEmpty.set(recycleAdapter.getAllItem().isEmpty())
        } else {
            recycleAdapter.addMoreItems(list)
        }
        done(recycleAdapter.itemCount - 1)
    }

    override fun onCleared() {
        super.onCleared()
        recycleAdapter.getAllItem().clear()
        previewAdapter.getAllItems().clear()
    }

}
