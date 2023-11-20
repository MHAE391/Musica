package com.m391.musica.utils

import android.graphics.Color
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.squareup.picasso.Picasso

object Binding {
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
                recyclerView.scrollToPosition(0)
            }
        }
    }

    @BindingAdapter(value = ["android:imageUrl", "android:imageId"], requireAll = false)
    @JvmStatic
    fun loadImage(imageView: ImageView, imageUrl: String, imageId: Long?) {
        val path = if (imageId != null && imageId == 0.toLong()) {
            imageUrl
        } else {
            "file://$imageUrl"
        }
        val circularProgressDrawable = CircularProgressDrawable(imageView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(Color.TRANSPARENT)
        circularProgressDrawable.start()
        Picasso.get().load(path)
            .placeholder(circularProgressDrawable)
            .into(imageView)
    }
}