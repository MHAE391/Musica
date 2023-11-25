package com.m391.musica.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.m391.musica.models.SongModel
class HomeViewModel(val deviceSongs: LiveData<List<SongModel>>) :
    ViewModel() {
}