package com.prashant.task.fragments.image


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
import com.prashant.task.databinding.ImageFragmentBinding
import com.prashant.task.singlton.MediaQuery

const val TAG = "Image"

class Image :Fragment() {
    private var _binding: ImageFragmentBinding? = null
    private val imageVM by viewModels<ImageVM>()
    private val binding get() = _binding!!

    private val args by navArgs<ImageArgs>()

    private val pickMultipleImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                Log.e(TAG, "$TAG: $uris")
                imageVM.updateAdapter(requireContext(), uris)
            }
            imageVM.showFab.set(false)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ImageFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = imageVM
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        argsHandled()
        with(binding) {
            fab.setOnClickListener {
                imageVM.showFab.get()?.let { bool -> displayFab(!bool) }
            }

            fabGallery.setOnClickListener {
                displayFab(false)
                pickMultipleImages.launch(MediaQuery.Image.value)
            }
            fabCamera.setOnClickListener {
                findNavController().navigate(ImageDirections.actionImageToCameraFragment(MediaQuery.Image))
            }
        }
    }

    private fun argsHandled() {
        args.uris?.list?.let {
            imageVM.recycleAdapter.addItems(it)
            imageVM.isAdapterEmpty.set(imageVM.recycleAdapter.getAllItem().isEmpty())
        }
    }

    override fun onResume() {
        super.onResume()
        displayFab(false)
    }

    private fun displayFab(boolean: Boolean) {
        imageVM.showFab.set(boolean)
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

