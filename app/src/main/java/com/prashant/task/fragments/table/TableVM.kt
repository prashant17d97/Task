package com.prashant.task.fragments.table

import android.view.View
import androidx.lifecycle.ViewModel
import com.prashant.task.R

class TableVM :ViewModel() {


    fun onClick(view: View) {
        when (view.id) {
            R.id.btnNext -> {}
            R.id.btnSave -> {}
        }
    }
}
