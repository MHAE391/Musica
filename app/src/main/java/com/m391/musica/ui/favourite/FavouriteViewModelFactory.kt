package com.m391.musica.ui.favourite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m391.musica.models.SongModel

class FavouriteViewModelFactory(
    private val app: Application,
    private val favouriteSongs: LiveData<List<SongModel>>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavouriteViewModel(app = app, favouriteSongs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}