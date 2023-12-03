package com.m391.musica.ui.shared_view_models

import android.app.Application
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.m391.musica.database.AppDatabase
import com.m391.musica.database.Music
import com.m391.musica.database.MusicDAO
import com.m391.musica.models.SongModel
import com.m391.musica.services.SongService
import com.m391.musica.utils.toDatabaseModel
import com.m391.musica.utils.toDisplayModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongsViewModel(
    private val app: Application,
    private val songService: SongService,
    private val musicDAO: MusicDAO
) :
    AndroidViewModel(app) {
    private val _deviceSongs = MutableLiveData<List<SongModel>>()
    val deviceSongs: LiveData<List<SongModel>> = _deviceSongs
    private val _favouriteSongs = MutableLiveData<List<SongModel>>()
    val favouriteSongs: LiveData<List<SongModel>> = _favouriteSongs


    suspend fun refreshSongs() = withContext(Dispatchers.IO) {
        _deviceSongs.postValue(
            songService.getAllSongs(app.applicationContext)
        )
    }

    suspend fun refreshFavouriteSongs() = withContext(Dispatchers.IO) {
        _favouriteSongs.postValue(musicDAO.getAll().toDisplayModel())
    }


    val setSongFavourite: suspend (Music) -> Unit =
        { music ->
            withContext(Dispatchers.IO) {
                musicDAO.insert(
                    music
                )
            }
        }
    val setSongNotFavourite: suspend (Music) -> Unit =
        { music ->
            withContext(Dispatchers.IO) {
                musicDAO.delete(music)
            }
        }

    val checkFavourite: suspend (Long) -> Boolean =
        { id ->
            withContext(Dispatchers.IO) {
                return@withContext musicDAO.getSongById(id) != null
            }
        }
}