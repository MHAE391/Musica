package com.m391.musica.ui.home

import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.m391.musica.models.SongModel

class HomeViewModel(private val deviceSongs: LiveData<List<SongModel>>) :
    ViewModel() {
    private val _songs = MutableLiveData<List<SongModel>>()
    val songs: LiveData<List<SongModel>> = _songs

    fun refreshSongs(viewLifecycleOwner: LifecycleOwner) {
        deviceSongs.observe(viewLifecycleOwner) {
            _songs.postValue(it)
        }
    }

    fun stopRefresh(viewLifecycleOwner: LifecycleOwner) {
        deviceSongs.removeObservers(viewLifecycleOwner)
    }

    fun searchListener(searchView: SearchView) {
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(quary: String?): Boolean {
                    if (quary != null) {
                        val songSearch = deviceSongs.value!!.filter {
                            it.artist.lowercase().contains(quary.lowercase())
                                    || it.title.lowercase().contains(quary.lowercase())
                                    || it.album.lowercase().contains(quary.lowercase())
                        }
                        _songs.postValue(songSearch)
                        return true
                    }
                    return false
                }

            }
        )
    }
}