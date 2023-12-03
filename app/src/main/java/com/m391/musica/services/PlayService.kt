package com.m391.musica.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleObserver
import com.m391.musica.R
import com.m391.musica.models.SongModel
import com.m391.musica.utils.Statics.ACTION_CANCEL
import com.m391.musica.utils.Statics.ACTION_NEXT
import com.m391.musica.utils.Statics.ACTION_PAUSE
import com.m391.musica.utils.Statics.ACTION_PLAY
import com.m391.musica.utils.Statics.ACTION_PREVIOUS
import com.m391.musica.utils.Statics.FOREGROUND_SERVICE_ID
import com.m391.musica.utils.Statics.MUSIC_SERVICE
import com.m391.musica.utils.Statics.NOTIFICATION_CHANNEL_ID

class PlayService : Service(), IPlayService, LifecycleObserver {
    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private var currentProgress: Long = 0
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var currentPlayList: List<SongModel>
    private var currentPlayingSong: SongModel? = null
    private lateinit var updateSong: (SongModel) -> Unit
    private lateinit var updatePlaying: (Boolean) -> Unit
    private lateinit var updateProgress: (Long) -> Unit
    private val handler = Handler(Looper.myLooper()!!)
    private val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                currentProgress += 10
                updateProgress(currentProgress)
                handler.postDelayed(this, 10)
            }
        }
    }


    inner class LocalBinder : Binder() {
        fun getService(): PlayService = this@PlayService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.action?.apply {
            handelAction(intent.action!!)
        }
        return START_STICKY
    }

    private fun handelAction(action: String) {
        when (action) {
            ACTION_NEXT -> {
                nextPrevious(1)
            }

            ACTION_PREVIOUS -> {
                nextPrevious(-1)
            }

            ACTION_PLAY -> {
                play()
            }

            ACTION_PAUSE -> {
                pause()
            }

            ACTION_CANCEL -> {
                pause()
                stopSelf()
            }
        }
    }

    override fun play() {
        isPlaying = true
        updatePlaying(true)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(baseContext, Uri.parse(currentPlayingSong!!.filePath))
            prepare()
            seekTo(currentProgress.toInt())
            start()
            setOnCompletionListener {
                pause()
                setNewProgress(0)
                nextPrevious(1)
            }
            handler.postDelayed(runnable, 10)
        }
        startForeground(
            FOREGROUND_SERVICE_ID, buildNotification(
                false
            ).build()
        )
    }

    override fun pause() {
        isPlaying = false
        updatePlaying(false)
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
        startForeground(
            FOREGROUND_SERVICE_ID, buildNotification(
                true
            ).build()
        )
    }

    fun setupData(
        song: (SongModel) -> Unit,
        playList: List<SongModel>,
        currentSong: SongModel,
        updatePlayingFun: (Boolean) -> Unit,
        progress: (Long) -> Unit,

        ) {
        updateSong = song
        currentPlayList = playList
        currentPlayingSong = currentSong
        updatePlaying = updatePlayingFun
        updateProgress = progress
    }

    override fun nextPrevious(value: Int) {
        val position = (currentPlayList.indexOf(currentPlayingSong) + value).mod(
            currentPlayList.size
        )
        currentPlayingSong = currentPlayList[position]
        updateSong(currentPlayingSong!!)
        setNewProgress(0)
        if (isPlaying) {
            pause()
            play()
        }
        startForeground(
            FOREGROUND_SERVICE_ID, buildNotification(
                !isPlaying
            ).build()
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(baseContext, MUSIC_SERVICE)
    }

    private fun buildNotification(
        playPause: Boolean
    ): NotificationCompat.Builder {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val playPausePendingIntent =
            if (playPause) getPlayPendingIntent(flag) else getPausePendingIntent(flag)
        val playPauseButton =
            if (playPause) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24
        val playPauseAction = if (playPause) ACTION_PLAY else ACTION_PAUSE
        return NotificationCompat.Builder(baseContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(currentPlayingSong!!.title).setContentText(currentPlayingSong!!.artist)
            .addAction(
                R.drawable.baseline_keyboard_double_arrow_left_24,
                ACTION_PREVIOUS,
                getPreviousPendingIntent(flag)
            ).addAction(
                playPauseButton, playPauseAction, playPausePendingIntent
            ).addAction(
                R.drawable.baseline_keyboard_double_arrow_right_24,
                ACTION_NEXT,
                getNextPendingIntent(flag)
            ).setSmallIcon(R.mipmap.lancher).setPriority(NotificationCompat.PRIORITY_LOW).setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(
                    mediaSession.sessionToken
                ).setShowCancelButton(true).setCancelButtonIntent(getCancelPendingIntent(flag))

            ).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun getPreviousPendingIntent(flag: Int): PendingIntent {
        val prevIntent = Intent(
            baseContext, PlayService::class.java
        ).apply {
            action = ACTION_PREVIOUS
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(baseContext, 0, prevIntent, flag)
        } else PendingIntent.getService(baseContext, 0, prevIntent, flag)
    }


    private fun getNextPendingIntent(flag: Int): PendingIntent {
        val nextIntent = Intent(baseContext, PlayService::class.java).apply {
            action = ACTION_NEXT
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(baseContext, 0, nextIntent, flag)
        } else PendingIntent.getService(baseContext, 0, nextIntent, flag)
    }

    private fun getPlayPendingIntent(flag: Int): PendingIntent {
        val playIntent = Intent(baseContext, PlayService::class.java).apply {
            action = ACTION_PLAY
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(baseContext, 0, playIntent, flag)
        } else PendingIntent.getService(baseContext, 0, playIntent, flag)
    }

    private fun getPausePendingIntent(flag: Int): PendingIntent {
        val pauseIntent = Intent(baseContext, PlayService::class.java).apply {
            action = ACTION_PAUSE
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(baseContext, 0, pauseIntent, flag)
        } else PendingIntent.getService(baseContext, 0, pauseIntent, flag)
    }

    private fun getCancelPendingIntent(flag: Int): PendingIntent {
        val cancelIntent = Intent(baseContext, PlayService::class.java).apply {
            action = ACTION_CANCEL
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(baseContext, 0, cancelIntent, flag)
        } else PendingIntent.getService(baseContext, 0, cancelIntent, flag)
    }

    fun getIsPlaying(): Boolean = isPlaying
    fun getCurrentPlaying(): SongModel? = currentPlayingSong

    fun setNewProgress(newProgress: Long) {
        currentProgress = newProgress
        updateProgress(newProgress)
        mediaPlayer?.seekTo(newProgress.toInt())
    }
}