package com.m391.musica.ui.player

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.widget.SeekBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m391.musica.database.Music
import com.m391.musica.models.SongModel
import com.m391.musica.services.IPlayService
import com.m391.musica.services.PlayService
import com.m391.musica.utils.toDatabaseModel
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val app: Application,
    private val startingSong: Int,
    private val deviceSongs: LiveData<List<SongModel>>,
    private val checkFavourite: suspend (Long) -> Boolean,
    private val songFavourite: suspend (Music) -> Unit,
    private val songNotFavourite: suspend (Music) -> Unit
) : ViewModel(), IPlayService, ServiceConnection {
    @SuppressLint("StaticFieldLeak")
    private lateinit var playService: PlayService
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying


    private val _playingSong = MutableLiveData<SongModel>()
    val playingSong: LiveData<SongModel> = _playingSong

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    private val _currentProgress = MutableLiveData<Long>()
    val currentProgress: LiveData<Long> = _currentProgress

    private val updateSong: (SongModel) -> Unit =
        { song ->
            _playingSong.postValue(song)
        }
    private val updatePlaying: (Boolean) -> Unit =
        { playing ->
            _isPlaying.postValue(playing)
        }

    private val updateProgress: (Long) -> Unit =
        { progress ->
            _currentProgress.postValue(progress)
        }

    init {
        val intentService = Intent(app, PlayService::class.java)
        app.bindService(intentService, this, BIND_AUTO_CREATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            app.startForegroundService(intentService)
        else
            app.startService(intentService)

        updateSong.invoke(getCurrentPlayingSong())
    }

    override fun play() {
        playService.play()
    }

    override fun pause() {
        playService.pause()
    }

    override fun nextPrevious(value: Int) {
        playService.nextPrevious(value)
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as PlayService.LocalBinder
        playService = binder.getService()
        val old = playService.getCurrentPlaying()
        playService.setupData(
            updateSong,
            deviceSongs.value!!,
            getCurrentPlayingSong(),
            updatePlaying,
            updateProgress
        )
        if (getCurrentPlayingSong() == old)
            _isPlaying.postValue(playService.getIsPlaying())
        else {
            playService.pause()
            playService.setNewProgress(0)
        }
    }

    fun setProgressListener(seekBar: SeekBar) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                if (fromUser) {
                    playService.setNewProgress(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })
    }


    override fun onServiceDisconnected(name: ComponentName?) {

    }

    private fun getCurrentPlayingSong(): SongModel {
        return deviceSongs.value!![startingSong]
    }

    suspend fun startCheckFavourite(viewLifecycleOwner: LifecycleOwner) {
        _playingSong.observe(viewLifecycleOwner) {
            if (it != null)
                viewModelScope.launch {
                    _isFavourite.postValue(checkFavourite.invoke(it.id))
                }
        }
    }

    fun stopCheckFavourite(viewLifecycleOwner: LifecycleOwner) {
        _playingSong.removeObservers(viewLifecycleOwner)
    }

    suspend fun setFavourite() {
        viewModelScope.launch {
            songFavourite(playingSong.value!!.toDatabaseModel())
        }
    }

    suspend fun setNotFavourite() {
        viewModelScope.launch {
            songNotFavourite(playingSong.value!!.toDatabaseModel())
        }
    }
}