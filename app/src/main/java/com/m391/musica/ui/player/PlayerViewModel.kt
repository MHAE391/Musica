package com.m391.musica.ui.player

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m391.musica.R
import com.m391.musica.models.SongModel
import com.m391.musica.services.MediaPlayerManger
import com.m391.musica.services.MediaPlayerManger.currentPlayList
import com.m391.musica.services.MediaPlayerManger.currentPlayingSong
import com.m391.musica.services.MediaPlayerManger.currentProgress
import com.m391.musica.services.MediaPlayerManger.mediaPlayer
import com.m391.musica.services.MediaPlayerManger.onNextPreviousPress
import com.m391.musica.services.MediaPlayerManger.pauseAudio
import com.m391.musica.services.MediaPlayerManger.playAudio
import com.m391.musica.services.MediaPlayerManger.setProgress
import com.m391.musica.services.MediaPlayerManger.showNotification
import com.m391.musica.services.PlayService
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val app: Application,
    startingSong: Int,
    private val deviceSongs: LiveData<List<SongModel>>,
    private val checkFavourite: suspend (Long) -> Boolean
) : ViewModel(), LifecycleObserver, ServiceConnection {

    val currentPlayingSong: LiveData<SongModel> = MediaPlayerManger.currentPlaying
    val isPlaying: LiveData<Boolean> = MediaPlayerManger.isPlaying
    val currentSongProgress: LiveData<Long> = currentProgress


    @SuppressLint("StaticFieldLeak")
    private lateinit var playService: PlayService


    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    init {
        currentPlayList = (deviceSongs.value)
        if (MediaPlayerManger.currentPlayingSong != deviceSongs.value!![startingSong]) {
            pauseAudio(app)
            currentProgress.postValue(0)
        }
        MediaPlayerManger.currentPlayingSong = (deviceSongs.value!![startingSong])
        MediaPlayerManger.currentPlaying.postValue(MediaPlayerManger.currentPlayingSong)
        currentPlayingSong.observeForever {
            viewModelScope.launch {
                _isFavourite.postValue(checkFavourite(it.id))
            }
        }
    }

    private fun startAudio() {
        playAudio(app.applicationContext, 0)
        startNotificationService()
    }

    private fun stopAudio() {
        pauseAudio(app.applicationContext)
    }

    fun onNextPreviousButtonClicked(value: Int, playPause: ImageView) {
        onNextPreviousPress(
            app.applicationContext,
            value,
            (playPause.tag == app.getString(R.string.pause))
        )
    }


    fun setProgressListener(seekBar: SeekBar) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                if (fromUser) {
                    setProgress(progress.toLong())
                    mediaPlayer?.seekTo(progress)
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


    fun setOnPauseButtonClicked() {
        stopAudio()
    }

    fun setOnPlayButtonClicked() {
        startAudio()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as PlayService.LocalBinder
        playService = binder.getService()
        showNotification(MediaPlayerManger.currentPlayingSong!!, 1, app.applicationContext)
    }

    override fun onServiceDisconnected(name: ComponentName?) {


    }


    private fun startNotificationService() {
        if (mediaPlayer != null) {
            val intentService = Intent(app, PlayService::class.java)
            app.bindService(intentService, this, BIND_AUTO_CREATE)
            app.startService(intentService)
        }
    }
}