package com.m391.musica.ui.player

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m391.musica.models.SongModel
import com.m391.musica.ui.home.HomeViewModel

class PlayerViewModelFactory(
    private val app: Application,
    private val currentPlaying: Int,
    private val deviceSongs: LiveData<List<SongModel>>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(app, currentPlaying, deviceSongs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}