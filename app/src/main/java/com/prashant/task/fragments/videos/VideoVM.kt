package com.prashant.task.fragments.videos

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.window.layout.WindowMetricsCalculator
import com.prashant.task.MainActivity
import com.prashant.task.R
import com.prashant.task.adapter.RecyclerAdapter
import com.prashant.task.databinding.MediaPreviewBinding
import com.prashant.task.fragments.mediamodel.MediaModel
import com.prashant.task.singlton.details

class VideoVM : ViewModel() {


    var isCaptured: Boolean = false
    val showFab = ObservableField(false)
    val isAdapterEmpty = ObservableField(false)
    val recycleAdapter = RecyclerAdapter<MediaModel>(R.layout.media_view)
    val currentVideo = MutableLiveData<Uri>()

    val previewAdapter = RecyclerAdapter<MediaModel>(R.layout.preview_view)


    init {
        isAdapterEmpty.set(recycleAdapter.getAllItem().isEmpty())
        previewAdapter.setOnItemClick { view, _, position ->
            when (view.id) {
                R.id.clPreview -> {
                    showFab.set(false)
                    currentVideo.value = previewAdapter.getItemAt(position).uri
                }
            }
        }
        recycleAdapter.setOnItemClick { view, _, position ->
            when (view.id) {
                R.id.clMediaCard -> {
                    showFab.set(false)
                    recycleAdapter.getItemAt(position).uri.let {
                        showVideoPreviewDialog(
                            (MainActivity.activity.get() as MainActivity),
                            recycleAdapter.getItemAt(position).uri,
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

    private fun showVideoPreviewDialog(
        activity: Activity,
        uri: Uri
    ) {
        val windowMetrics =
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
        val currentBounds = windowMetrics.bounds
        val width = currentBounds.width()
        val height = currentBounds.height()
        val builder = AlertDialog.Builder(activity, R.style.TransparentDialog)

        val dialogBinding = MediaPreviewBinding.inflate(LayoutInflater.from(activity))


        builder.setView(dialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        with(dialogBinding) {

            clMediaPreView.minWidth = (width * 0.85).toInt()
            clMediaPreView.minHeight = (height * 0.85).toInt()
            clMediaPreView.background = ContextCompat.getDrawable(activity, R.drawable.containerbg)
            /**
             * Visibility
             * */
            imagePreview.isVisible = false
            videoPreview.isVisible = true
            ivCancelPreview.isVisible = true
            val player = ExoPlayer.Builder(videoPreview.context).build()
            videoPreview.player = player
            // Build the media item.
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()

            ivCancelPreview.setOnClickListener {
                alertDialog.dismiss()
                player.release()
            }
        }
        alertDialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        alertDialog.show()
    }
}
