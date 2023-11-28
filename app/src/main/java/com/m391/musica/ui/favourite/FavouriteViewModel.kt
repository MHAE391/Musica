package com.m391.musica.ui.favourite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m391.musica.database.AppDatabase
import com.m391.musica.database.Music
import com.m391.musica.database.MusicDAO
import com.m391.musica.models.SongModel
import com.m391.musica.utils.toDisplayModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel(app: Application, private val favouriteSongs: LiveData<List<SongModel>>) :
    AndroidViewModel(app) {

    private val _songs = MutableLiveData<List<SongModel>>()
    val songs: LiveData<List<SongModel>> = _songs

    fun refreshSongs(viewLifecycleOwner: LifecycleOwner) {
        favouriteSongs.observe(viewLifecycleOwner) {
            _songs.postValue(it)
        }
    }

    fun stopRefresh(viewLifecycleOwner: LifecycleOwner) {
        favouriteSongs.removeObservers(viewLifecycleOwner)
    }
}