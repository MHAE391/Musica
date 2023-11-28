package com.m391.musica.ui.player

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
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
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val app: Application,
    selectedPlaying: Int,
    private val deviceSongs: LiveData<List<SongModel>>,
    private val checkFavourite: suspend (Long) -> Boolean
) : ViewModel(), LifecycleObserver {
    private val currentPlaying = MutableLiveData<Int>()
    private val _currentPlayingSong = MutableLiveData<SongModel>()
    val currentPlayingSong: LiveData<SongModel> = _currentPlayingSong
    private val _currentProgress = MutableLiveData<Long>()
    val currentProgress: LiveData<Long> = _currentProgress
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.myLooper()!!)
    private val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                setProgress(mediaPlayer!!.currentPosition.toLong())
                handler.postDelayed(this, 10)
            }
        }
    }

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    init {
        currentPlaying.postValue(selectedPlaying)
        _currentPlayingSong.postValue(deviceSongs.value!![selectedPlaying])
        _currentProgress.postValue(0)
        _currentPlayingSong.observeForever {
            viewModelScope.launch {
                _isFavourite.postValue(checkFavourite(it.id))
            }
        }
    }

    private fun startAudio(playPause: ImageView, startingPoint: Int, uri: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(app.applicationContext, Uri.parse(uri))
            prepare()
            seekTo(startingPoint)
            start()
            setOnCompletionListener {
                pauseAudio()
                setProgress(0)
                onNextPreviousButtonClicked(1, playPause)
            }
            pauseAudio()
            handler.postDelayed(runnable, 10)
        }
    }

    private fun pauseAudio() {
        handler.removeCallbacks(runnable)
        if (mediaPlayer != null) {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        }
        mediaPlayer = null
    }

    fun onNextPreviousButtonClicked(value: Int, playPause: ImageView) {
        val songNumber = currentPlaying.value!!.plus(value).mod(deviceSongs.value!!.size)
        val song = deviceSongs.value!![songNumber]
        currentPlaying.postValue(songNumber)
        _currentPlayingSong.postValue(song)
        setOnNextPreviousClick(playPause, song.filePath)
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

    private fun setProgress(value: Long) {
        _currentProgress.postValue(value)
    }

    private fun setPlayPauseButtonImage(playPause: ImageView, tag: String, image: Int) {
        playPause.apply {
            setImageResource(image)
            this.tag = tag
        }
    }

    private fun setOnNextPreviousClick(playPause: ImageView, uri: String) {
        pauseAudio()
        setProgress(0)
        if (playPause.tag == app.getString(R.string.pause)) {
            startAudio(playPause, 0, uri)
        }
    }

    fun setOnPauseButtonClicked(playPause: ImageView) {
        setPlayPauseButtonImage(
            playPause, app.getString(R.string.play), R.drawable.baseline_play_arrow_24
        )
        pauseAudio()
    }

    fun setOnPlayButtonClicked(playPause: ImageView) {
        setPlayPauseButtonImage(
            playPause, app.getString(R.string.pause), R.drawable.baseline_pause_24
        )
        startAudio(playPause, currentProgress.value!!.toInt(), currentPlayingSong.value!!.filePath)
    }


}