package com.m391.musica.utils

import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.m391.musica.R
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

    @BindingAdapter("android:imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, imageUrl: String) {
        val circularProgressDrawable = CircularProgressDrawable(imageView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(Color.TRANSPARENT)
        circularProgressDrawable.start()
        val image = getAlbumArt(imageUrl)
        if (image != null)
            Glide.with(imageView.context).asBitmap().placeholder(circularProgressDrawable)
                .load(image).into(imageView)
        else
            Glide.with(imageView.context).load(R.drawable.music).into(imageView)

    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}