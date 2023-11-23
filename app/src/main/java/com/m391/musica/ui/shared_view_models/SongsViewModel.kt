package com.m391.musica.ui.shared_view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.m391.musica.models.SongModel
import com.m391.musica.services.SongService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongsViewModel(app: Application, songService: SongService) : AndroidViewModel(app) {
    private val _deviceSongs = MutableLiveData<List<SongModel>>()
    val deviceSongs: LiveData<List<SongModel>> = _deviceSongs

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _deviceSongs.postValue(
                    songService.getAllSongs(app.applicationContext)
                )
            }
        }
    }

}