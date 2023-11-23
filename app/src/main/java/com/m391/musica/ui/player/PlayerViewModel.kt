package com.m391.musica.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.m391.musica.models.SongModel

class PlayerViewModel(selectedPlaying: Int, deviceSongs: LiveData<List<SongModel>>) : ViewModel() {
    private val currentPlaying = MutableLiveData<Int>()
    private val _currentPlayingSong = MutableLiveData<SongModel>()
    val currentPlayingSong: LiveData<SongModel> = _currentPlayingSong

    init {
        currentPlaying.postValue(selectedPlaying)
        _currentPlayingSong.postValue(deviceSongs.value!![selectedPlaying])
    }

}