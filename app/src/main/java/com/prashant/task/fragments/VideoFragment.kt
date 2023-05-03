package com.prashant.task.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.prashant.task.R
import com.prashant.task.databinding.VideoFragmentBinding


class VideoFragment : Fragment() {
    private var _binding: VideoFragmentBinding? = null
    private val binding get() = _binding!!
    private var boolean: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = VideoFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            fab.setOnClickListener {
                boolean = !boolean
                displayFab(boolean)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        displayFab(false)
    }

    private fun displayFab(boolean: Boolean) {
        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        val slideDown: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        with(binding) {
            fabCamera.isVisible = boolean
            tvCamera.isVisible = boolean
            tvCamera.startAnimation(slideUp.takeIf { boolean } ?: slideDown)
            fabCamera.startAnimation(slideUp.takeIf { boolean } ?: slideDown)

            fabGallery.isVisible = boolean
            tvGallery.isVisible = boolean
            tvGallery.startAnimation(slideUp.takeIf { boolean } ?: slideDown)
            fabGallery.startAnimation(slideUp.takeIf { boolean } ?: slideDown)
        }
    }

}