package com.prashant.task.fragments.videos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.prashant.task.R
import com.prashant.task.databinding.VideoFragmentBinding
import com.prashant.task.fragments.image.ImageDirections
import com.prashant.task.singlton.MediaQuery

const val TAG = "Videos"

class VideoFragment :Fragment() {
    private var _binding: VideoFragmentBinding? = null
    private val videoVM by viewModels<VideoVM>()
    private val binding get() = _binding!!
    private val args by navArgs<VideoFragmentArgs>()


    private val pickMultipleImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                Log.e(TAG, "$TAG: $uris")
                videoVM.updateAdapter(requireContext(), uris)
            }
            videoVM.showFab.set(false)
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
        argsHandled()
        with(binding) {
            fab.setOnClickListener {
                videoVM.showFab.get()?.let { bool -> displayFab(!bool) }
            }

            fabGallery.setOnClickListener {
                displayFab(false)
                pickMultipleImages.launch(MediaQuery.Video.value)
            }
            fabCamera.setOnClickListener {
                fabCamera.setOnClickListener {
                    findNavController().navigate(
                        ImageDirections.actionImageToCameraFragment(
                            MediaQuery.Video
                        )
                    )
                }
            }
        }
    }

    private fun argsHandled() {
        args.uris?.list?.let { videoVM.recycleAdapter.addItems(it) }
    }

    override fun onResume() {
        super.onResume()
        videoVM.showFab.set(false)
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
}