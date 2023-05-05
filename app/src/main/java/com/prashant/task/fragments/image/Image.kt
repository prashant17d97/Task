package com.prashant.task.fragments.image


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
import com.prashant.task.R
import com.prashant.task.databinding.ImageFragmentBinding
import com.prashant.task.singlton.PermissionUtils
import com.prashant.task.singlton.PermissionUtils.hasPermissions
import com.prashant.task.singlton.PermissionUtils.isPermissionsGranted
import com.prashant.task.singlton.SingletonObj.MediaQuery
import com.prashant.task.singlton.SingletonObj.details
import com.prashant.task.singlton.SingletonObj.loadImageIn
import com.prashant.task.singlton.SingletonObj.showCustomDialog
import java.io.File


const val TAG = "Image"

class Image : Fragment() {
    private var _binding: ImageFragmentBinding? = null
    private val imageVM by viewModels<ImageVM>()
    private val binding get() = _binding!!

    private val pickMultipleImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                Log.e(TAG, "$TAG: $uris")
                imageVM.updateAdapter(requireContext(), uris) {
                    binding.rvVideos.smoothScrollToPosition(it)
                }
            }
            imageVM.showFab.set(false)
        }
    private var latestTmpUri: Uri? = null

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts
            .RequestMultiplePermissions()
    ) {
        if (isPermissionsGranted(requireContext(), it)) {
            launchCamera()
        }
    }
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                latestTmpUri?.let { uri ->
                    imageVM.previewAdapter.addItem(details(requireContext(), uri))
                    imageVM.currentImage.postValue(uri)
                }
            }
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
        with(binding) {
            fab.setOnClickListener {
                imageVM.showFab.get()?.let { bool -> displayFab(!bool) }
            }

            fabGallery.setOnClickListener {
                displayFab(false)
                pickMultipleImages.launch(MediaQuery.Image.value)
            }
            /**
             * Calling [showCustomDialog] to capture the images and preview the before adding into the list
             * */
            fabCamera.setOnClickListener {
                displayFab(false)
                showCustomDialog(requireActivity()) { binding, dialog ->

                    with(binding) {
                        recyclerView.adapter = imageVM.previewAdapter
                        binding.videoPreview.isVisible = false
                        imageVM.currentImage.observe(viewLifecycleOwner) { uri ->
                            uri loadImageIn ivPreview
                        }

                        ivAddMore.setOnClickListener {
                            imageVM.isCaptured = false
                            launchCamera()
                        }

                        ivDone.setOnClickListener {
                            //Added the images in main list adapter from preview and clear the preview
                            imageVM.recycleAdapter.addMoreItems(imageVM.previewAdapter.getAllItems())

                            imageVM.isAdapterEmpty.set(
                                imageVM.recycleAdapter.getAllItem().isEmpty()
                            )
                            imageVM.previewAdapter.getAllItems().clear()
                            imageVM.currentImage.postValue(null)
                            dialog.dismiss()
                            this@Image.binding.rvVideos.smoothScrollToPosition(imageVM.recycleAdapter.itemCount - 1)
                        }
                        ivCancel.setOnClickListener {
                            imageVM.currentImage.postValue(null)
                            imageVM.previewAdapter.getAllItems().clear()
                            dialog.dismiss()
                        }
                    }

                    dialog.show()

                }
            }
        }
    }

    private fun launchCamera() {
        if (hasPermissions(requireContext(), arrayOf(Manifest.permission.CAMERA))) {
            takeImage()
        } else {
            cameraPermission.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    /**
     * Displays or hides the camera and gallery FABs and their associated text views with an animation.
     * @param boolean True to display the FABs and text views, false to hide them.
     */
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

    /**
     * Launches the device camera to capture an image and save it to a temporary file.
     * Sets the [isCaptured] flag to true.
     * @throws ActivityNotFoundException if the device doesn't have a camera app to handle the intent
     */
    private fun takeImage() {
        imageVM.isCaptured = true
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takePicture.launch(uri)
            }
        }
    }


    /**
     * [getTmpFileUri] create the temporary uri file location in the cache and it add the image at
     * temporary location from camera.
     * */
    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("Image", ".png", requireContext().cacheDir)
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