package com.prashant.task.fragments.inputs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.prashant.task.databinding.UserInputFragmentBinding
import com.prashant.task.singlton.SingletonObj.clearFocus


class UserInput :Fragment() {

    private var _binding: UserInputFragmentBinding? = null
    private val binding get() = _binding!!
    private val userInputVM by viewModels<UserInputVM>()

    companion object {
        private const val TAG = "UserInput"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = UserInputFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = userInputVM
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            etTestFiled.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Handle the ActionDone event here
                    if (userInputVM.validateInputs) {
                        requireActivity().clearFocus()
                    }
                    return@setOnEditorActionListener true
                }
                false
            }

            userInputVM.errorFinder.observe(viewLifecycleOwner) {
                it?.let {
                    val focusOn = when (it) {
                        FieldTypoError.FullName -> etFullName
                        FieldTypoError.Address -> etAddress
                        FieldTypoError.Date -> etDate
                        FieldTypoError.Mobile -> etMobile
                        FieldTypoError.Location -> etLocation
                        FieldTypoError.Description -> etDescription
                        FieldTypoError.TestFiled -> etTestFiled
                    }
                    focusOn.requestFocus()
                    Toast.makeText(
                        requireContext(), it.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}