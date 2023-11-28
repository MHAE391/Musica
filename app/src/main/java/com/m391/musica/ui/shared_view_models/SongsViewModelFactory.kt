package com.m391.musica.ui.shared_view_models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m391.musica.database.MusicDAO
import com.m391.musica.services.SongService

class SongsViewModelFactory(
    private val app: Application,
    private val songService: SongService,
    private val musicDAO: MusicDAO
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SongsViewModel(app, songService, musicDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}