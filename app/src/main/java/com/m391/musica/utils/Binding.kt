package com.m391.musica.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.m391.musica.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.Duration

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
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        if (imageUrl != null) {
            val circularProgressDrawable = CircularProgressDrawable(imageView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.setColorSchemeColors(Color.TRANSPARENT)
            circularProgressDrawable.start()
            val image = getAlbumArt(imageUrl)
            if (image != null) Glide.with(imageView.context).asBitmap()
                .placeholder(circularProgressDrawable).load(image).into(imageView)
            else Glide.with(imageView.context).load(R.drawable.back).into(imageView)
        }
    }

    fun getAlbumArt(uri: String): ByteArray? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            retriever.release()
            art
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    @BindingAdapter("android:time")
    @JvmStatic
    fun setTime(textView: TextView, time: Long) {
        textView.text = convertDuration(time)
    }

    private fun convertDuration(durationInMillis: Long): String {
        val seconds = (durationInMillis / 1000).toInt()
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        val formattedHours = if (hours > 0) "$hours:" else ""
        val formattedMinutes = String.format("%02d", minutes)
        val formattedSeconds = String.format("%02d", remainingSeconds)

        return "$formattedHours$formattedMinutes:$formattedSeconds"
    }

    @BindingAdapter("android:seek_time")
    @JvmStatic
    fun setSeekTime(seekBar: SeekBar, durationInMillis: Long) {
        if (durationInMillis != (0).toLong()) {
            seekBar.max = durationInMillis.toInt()
        }
    }

    @BindingAdapter("android:progress_time")
    @JvmStatic
    fun setProgressTime(seekBar: SeekBar, durationInMillis: Long) {
        seekBar.progress = durationInMillis.toInt()
    }

}
