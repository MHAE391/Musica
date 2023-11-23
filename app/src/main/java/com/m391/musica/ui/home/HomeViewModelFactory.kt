package com.m391.musica.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m391.musica.models.SongModel
import com.m391.musica.services.SongService

class HomeViewModelFactory(private val deviceSongs: LiveData<List<SongModel>>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(deviceSongs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}