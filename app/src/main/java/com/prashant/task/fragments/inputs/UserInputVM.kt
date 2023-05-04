package com.prashant.task.fragments.inputs

import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.prashant.task.MainActivity
import com.prashant.task.R
import com.prashant.task.singlton.SingletonObj.clearFocus
import com.prashant.task.singlton.SingletonObj.showToast
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

class UserInputVM :ViewModel() {

    val errorFinder = MutableLiveData<FieldTypoError>()
    private val dateRegex = Regex("^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/\\d{4}$")
    private val mobileRegex = Regex("^\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{4}$")
    val fullName = ObservableField("")
    val address = ObservableField("")
    val date = ObservableField("")
    val mobile = ObservableField("")
    val location = ObservableField("")
    val description = ObservableField("")
    val testFiled = ObservableField("")
    private var inputData: InputData? = null

    companion object {
        private const val TAG = "UserInputVM"
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnNext -> view.findNavController()
                .navigate(UserInputDirections.actionUserInputToTable(inputData))

            R.id.btnSave -> {
                if (validateInputs) {
                    saveData()
                }
            }
        }
    }

    val validateInputs: Boolean
        get() = when {
            fullName.get()?.trim()?.isEmpty() == true -> {
                errorFinder.value = FieldTypoError.FullName
                false
            }

            address.get()?.trim()?.isEmpty() == true -> {
                errorFinder.value = FieldTypoError.Address
                false
            }

            date.get()?.trim()?.matches(dateRegex) == false -> {
                errorFinder.value = FieldTypoError.Date
                false
            }

            mobile.get()?.trim()?.matches(mobileRegex) == false -> {
                errorFinder.value = FieldTypoError.Mobile
                false
            }

            location.get()?.trim()?.isEmpty() == true -> {
                errorFinder.value = FieldTypoError.Location
                false
            }

            description.get()?.trim()?.isEmpty() == true -> {
                errorFinder.value = FieldTypoError.Description
                false
            }

            testFiled.get()?.trim()?.isEmpty() == true -> {
                errorFinder.value = FieldTypoError.TestFiled
                false
            }

            else -> true
        }


    private fun saveData() {
        val activity = MainActivity.activity.get() as MainActivity
        if (activity.currentFocus?.hasFocus() == true) {
            activity.clearFocus()
        }

        /**
         * First approach to create Json
         * */
        val jsonObject = JSONObject().apply {
            put("fullName", fullName.get()?.trim())
            put("address", address.get()?.trim())
            put("date", date.get()?.trim())
            put("mobile", mobile.get()?.trim())
            put("location", location.get()?.trim())
            put("description", description.get()?.trim())
            put("testFile", testFiled.get()?.trim())
        }

        /**
         * Second approach to create Json
         * */
        inputData = InputData(
            fullName = fullName.get()?.trim() ?: "",
            address = address.get()?.trim() ?: "",
            date = date.get()?.trim() ?: "",
            mobile = mobile.get()?.trim() ?: "",
            location = location.get()?.trim() ?: "",
            description = description.get()?.trim() ?: "",
            testFile = testFiled.get()?.trim() ?: ""
        )
        val jsonString = Gson().toJson(inputData)

        Log.e(TAG, "saveData: \nApproach 1:---->$jsonObject\nApproach 2:---->$jsonString")

        fullName.set("")
        address.set("")
        date.set("")
        mobile.set("")
        location.set("")
        description.set("")
        testFiled.set("")
        showToast("Data saved successfully!")
    }
}

enum class FieldTypoError(val message: String) {
    FullName("Name is not allowed to empty!"),
    Address("Address is not allowed to empty!"),
    Date("Enter a valid date and must match DD/MM/YYYY!"),
    Mobile("Enter a valid Mobile number!"),
    Location("Location field is not allowed to empty!"),
    Description("Description field is not allowed to empty!"),
    TestFiled("TestFiled field is not allowed to empty!"),
}

@Parcelize
data class InputData(
    val fullName: String,
    val address: String,
    val date: String,
    val mobile: String,
    val location: String,
    val description: String,
    val testFile: String,
) :Parcelable