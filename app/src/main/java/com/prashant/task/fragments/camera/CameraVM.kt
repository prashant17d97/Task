package com.prashant.task.fragments.camera

import android.net.Uri
import android.os.Parcelable
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.prashant.task.R
import com.prashant.task.adapter.RecyclerAdapter
import com.prashant.task.fragments.mediamodel.MediaModel
import com.prashant.task.singlton.MediaQuery
import kotlinx.parcelize.Parcelize

class CameraVM :ViewModel() {
    val currentImage = ObservableField<Uri>()

    var isCaptured: Boolean = false
    var comingFrom: MediaQuery = MediaQuery.Image
    val previewAdapter = RecyclerAdapter<MediaModel>(R.layout.preview_view)

    init {
        previewAdapter.setOnItemClick { view, _, position ->
            when (view.id) {
                R.id.clPreview -> currentImage.set(previewAdapter.getItemAt(position).uri)
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.ivDone -> {
                when (comingFrom) {
                    MediaQuery.Image -> view.findNavController()
                        .navigate(
                            CameraFragmentDirections.actionCameraFragmentToImage(
                                URI
                                    (previewAdapter.getAllItems())
                            )
                        )

                    MediaQuery.Video -> view.findNavController()
                        .navigate(
                            CameraFragmentDirections.actionCameraFragmentToImage(
                                URI
                                    (previewAdapter.getAllItems())
                            )
                        )
                }
            }
        }
    }
}

@Parcelize
data class URI(
    val list: List<MediaModel>
) :Parcelable
