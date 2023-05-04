package com.prashant.task.fragments.inputs

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.prashant.task.R
import org.json.JSONObject

class UserInputVM : ViewModel() {

    val fullName = ObservableField("")
    val address = ObservableField("")
    val date = ObservableField("")
    val mobile = ObservableField("")
    val location = ObservableField("")
    val description = ObservableField("")
    val testFiled = ObservableField("")

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnNext -> {}
            R.id.btnSave -> {
                saveData()
            }
        }
    }

    private fun saveData() {
        val jsonObject = JSONObject()
        jsonObject.put("name", "Prashant")
        jsonObject.put("address", "Address")

        /**
         * Step 1: data class MyObject(val name: String, val address: String)
         * Step 2: val myObject = MyObject("Prashant", "Address")
         * Step 3: val gson = Gson()
         * Step 4: val jsonString = gson.toJson(myObject)
         * */

    }
}
