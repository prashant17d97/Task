package com.prashant.task.fragments.videos

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.prashant.task.R
import com.prashant.task.databinding.VideoFragmentBinding
import com.prashant.task.singlton.PermissionUtils
import com.prashant.task.singlton.SingletonObj.MediaQuery
import com.prashant.task.singlton.SingletonObj.details
import com.prashant.task.singlton.SingletonObj.showCustomDialog
import java.io.File

const val TAG = "Videos"

class VideoFragment :Fragment() {
    private var _binding: VideoFragmentBinding? = null
    private val videoVM by viewModels<VideoVM>()
    private var player: ExoPlayer? = null
    private val binding get() = _binding!!
    private var latestTmpUri: Uri? = null


    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts
            .RequestMultiplePermissions()
    ) {
        if (PermissionUtils.isPermissionsGranted(requireContext(), it)) {
            requestPermission()
        }
    }

    private val pickMultipleVideos =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                Log.e(TAG, "$TAG: $uris")
                videoVM.updateAdapter(requireContext(), uris) {
                    binding.rvVideos.smoothScrollToPosition(it)
                }
            }
            videoVM.showFab.set(false)
        }

    private val captureVideo =
        registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success) {
                latestTmpUri?.let { uri ->
                    videoVM.currentVideo.value = uri
                    videoVM.previewAdapter.addItem(details(requireContext(), uri))
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = VideoFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = videoVM
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            fab.setOnClickListener {
                videoVM.showFab.get()?.let { bool -> displayFab(!bool) }
            }

            fabGallery.setOnClickListener {
                displayFab(false)
                pickMultipleVideos.launch(MediaQuery.Video.value)
            }
            fabCamera.setOnClickListener {
                displayFab(false)
                showCustomDialog(requireActivity()) { binding, dialog ->
                    with(binding) {
                        recyclerView.adapter = videoVM.previewAdapter
                        ivPreview.isVisible = false
                        videoPreview.isVisible = true
                        videoVM.currentVideo.observe(viewLifecycleOwner) {
                            it?.let { uri ->
                                if (player == null) {
                                    player = ExoPlayer.Builder(videoPreview.context).build()
                                    videoPreview.player = player
                                }
                                if (player?.isPlaying == true) {
                                    player?.pause()
                                }
                                val mediaItem = MediaItem.fromUri(uri)
                                player?.setMediaItem(mediaItem)
                                player?.prepare()
                                player?.play()
                            }
                        }

                        ivAddMore.setOnClickListener {
                            videoVM.isCaptured = false
                            player?.pause()
                            requestPermission()
                        }
                        ivDone.setOnClickListener {
                            videoVM.recycleAdapter.addMoreItems(videoVM.previewAdapter.getAllItems())
                            videoVM.currentVideo.value = null
                            videoVM.previewAdapter.getAllItems().clear()
                            videoVM.isAdapterEmpty.set(
                                videoVM.recycleAdapter.getAllItem().isEmpty()
                            )
                            dialog.dismiss()
                            player?.pause()
                            player?.release()
                            player = null
                            this@VideoFragment.binding.rvVideos.smoothScrollToPosition(videoVM.recycleAdapter.itemCount - 1)
                        }
                        ivCancel.setOnClickListener {
                            videoVM.currentVideo.value = null
                            player?.release()
                            player = null
                            videoVM.previewAdapter.getAllItems().clear()
                            dialog.dismiss()
                        }
                    }

                    dialog.show()

                }
            }
        }
    }

    private fun requestPermission() {
        if (PermissionUtils.hasPermissions(requireContext(), arrayOf(Manifest.permission.CAMERA))) {
            captureVideo()
        } else {
            cameraPermission.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    private fun captureVideo() {
        videoVM.isCaptured = true
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                captureVideo.launch(uri)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        displayFab(false)
    }

    private fun displayFab(boolean: Boolean) {
        videoVM.showFab.set(boolean)
        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        val slideDown: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        with(binding) {
            tvCamera.startAnimation(slideUp.takeIf { boolean } ?: slideDown)
            fabCamera.startAnimation(slideUp.takeIf { boolean } ?: slideDown)
            tvGallery.startAnimation(slideUp.takeIf { boolean } ?: slideDown)
            fabGallery.startAnimation(slideUp.takeIf { boolean } ?: slideDown)
        }
    }


    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("Video", ".mp4", requireContext().cacheDir)
            .apply {
                createNewFile()
                deleteOnExit()
            }

        return FileProvider.getUriForFile(
            requireActivity().applicationContext,
            "com.prashant.task.provider",
            tmpFile
        )
    }
}