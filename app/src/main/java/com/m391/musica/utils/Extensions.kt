package com.m391.musica.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.m391.musica.database.Music
import com.m391.musica.models.SongModel

fun <T> RecyclerView.setupGridRecycler(
    adapter: BaseRecyclerViewAdapter<T>
) {
    this.apply {
        layoutManager = GridLayoutManager(this.context, 3)
        this.adapter = adapter
    }
}

fun <T> RecyclerView.setupLinearRecycler(
    adapter: BaseRecyclerViewAdapter<T>
) {
    this.apply {
        layoutManager = LinearLayoutManager(this.context)
        this.adapter = adapter
    }
}

fun List<Music>.toDisplayModel(): List<SongModel> {
    return map { music ->
        SongModel(
            id = music.id,
            title = music.title,
            artist = music.artist,
            duration = music.duration,
            album = music.album,
            filePath = music.filePath
        )
    }
}

fun SongModel.toDatabaseModel(): Music {
    return Music(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        filePath = filePath
    )
}
