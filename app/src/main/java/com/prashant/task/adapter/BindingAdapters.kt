package com.prashant.task.adapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prashant.task.R

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
        Glide.with(imageView.context)
            .load(uri)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(imageView)
    }

}