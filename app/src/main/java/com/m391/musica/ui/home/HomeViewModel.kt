package com.m391.musica.ui.home

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.m391.musica.models.SongModel
import com.m391.musica.services.SongService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(val deviceSongs: LiveData<List<SongModel>>) :
    ViewModel() {
}