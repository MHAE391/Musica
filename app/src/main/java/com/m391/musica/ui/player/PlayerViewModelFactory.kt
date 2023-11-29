package com.m391.musica.ui.player

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m391.musica.models.SongModel

class PlayerViewModelFactory(
    private val app: Application,
    private val song: Int,
    private val deviceSongs: LiveData<List<SongModel>>,
    private val checkFavourite: suspend (Long) -> Boolean
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(app, song, deviceSongs, checkFavourite) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}