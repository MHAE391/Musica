package com.m391.musica.utils

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.m391.musica.models.SongModel
import androidx.databinding.library.baseAdapters.BR


class DataBindingViewHolder<T>(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: T) {
        when (item) {
            is SongModel -> binding.setVariable(BR.song, item)
        }
        binding.executePendingBindings()
    }
}