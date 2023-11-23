package com.m391.musica.ui.home

import com.m391.musica.R
import com.m391.musica.models.SongModel
import com.m391.musica.utils.BaseRecyclerViewAdapter

class SongAdapter(callback: (song: SongModel) -> Unit) :
    BaseRecyclerViewAdapter<SongModel>(callback) {
    override fun getLayoutRes(viewType: Int): Int = R.layout.song_item
}