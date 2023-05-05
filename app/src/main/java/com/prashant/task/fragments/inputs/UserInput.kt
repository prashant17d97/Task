package com.prashant.task.fragments.inputs

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.prashant.task.R
import com.prashant.task.databinding.UserInputFragmentBinding
import com.prashant.task.singlton.SingletonObj.clearFocus
import com.prashant.task.singlton.SingletonObj.showToast
import java.util.Calendar


class UserInput : Fragment() {

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
        val locations = resources.getStringArray(R.array.locations)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, locations)
        with(binding) {
            etLocation.setAdapter(adapter)

            //Handling onActionDone Event
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

            //Exposed the calender on focused
            etDate.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showDatePickerDialog()
                }
            }

            //Exposed the DropDown on focused to selected the location from locations menu
            etLocation.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    etLocation.showDropDown()
                }
            }
            etLocation.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                // Get the selected item
                val selectedItem = etLocation.adapter.getItem(position) as String

                // Set the selected item to the ViewModel
                if (userInputVM.location.get() == selectedItem) {
                    // Shift focus to the next field
                    etDescription.requestFocus()
                }
            }

            //Handle the input entry errors or does not matches requirements.
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
                    if (focusOn == etLocation) {
                        etLocation.showDropDown()
                    } else {
                        focusOn.requestFocus()
                    }
                    showToast(it.message)
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, yearSelected, monthOfYear, dayOfMonth ->
                val date = "${String.format("%02d", dayOfMonth)}/${
                    String.format(
                        "%02d",
                        monthOfYear + 1
                    )
                }/$yearSelected"
                Log.e(TAG, "showDatePickerDialog: $date")
                userInputVM.date.set(date)
                binding.etMobile.requestFocus()
            }, year, month, day)
        datePickerDialog.show()
    }

}