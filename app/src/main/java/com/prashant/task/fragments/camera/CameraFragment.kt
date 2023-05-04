package com.prashant.task.fragments.camera

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.prashant.task.databinding.CameraFragmentBinding
import com.prashant.task.fragments.mediamodel.MediaModel
import com.prashant.task.singlton.bytesToMb
import com.prashant.task.singlton.milliSecondsToDate
import java.io.File

const val TAG = "Camera"

class CameraFragment :Fragment() {
    private var _binding: CameraFragmentBinding? = null
    private val binding get() = _binding!!
    private val cameraVM by viewModels<CameraVM>()
    private val args by navArgs<CameraFragmentArgs>()
    private var latestTmpUri: Uri? = null

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts
            .RequestPermission()
    ) {
        if (it) {
            takeImage()
        }
    }
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                latestTmpUri?.let { uri ->
                    cameraVM.currentImage.set(uri)
                    cameraVM.previewAdapter.addItem(details(requireContext(), uri))
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CameraFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = cameraVM
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraVM.comingFrom = args.comingFor
        binding.ivAddMore.setOnClickListener {
            cameraVM.isCaptured = false
            takeImage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (!cameraVM.isCaptured) {
            requestPermission()
        }
        Log.e(TAG, "onResume: ${args.comingFor}")
    }

    private fun requestPermission() {
        if (requireContext().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takeImage()
        } else {
            cameraPermission.launch(CAMERA)
        }
    }

    private fun takeImage() {
        cameraVM.isCaptured = true
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takePicture.launch(uri)
            }
        }
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

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("task", ".png", requireContext().cacheDir)
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