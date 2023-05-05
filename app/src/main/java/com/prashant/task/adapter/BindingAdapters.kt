package com.prashant.task.adapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prashant.task.singlton.SingletonObj.loadImageIn

/** Binding Adapters */
object BindingAdapters {

    @BindingAdapter(value = ["setRecyclerAdapter"], requireAll = false)
    @JvmStatic
    fun setRecyclerAdapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
    ) {
        recyclerView.adapter = adapter
    }

    @BindingAdapter(value = ["setImage"], requireAll = false)
    @JvmStatic
    fun setImage(
        imageView: ImageView,
        uri: Uri?
    ) {
        uri loadImageIn imageView
    }
}