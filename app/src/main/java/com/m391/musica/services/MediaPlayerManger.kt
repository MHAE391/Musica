package com.m391.musica.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks.await
import com.m391.musica.R
import com.m391.musica.models.SongModel
import com.m391.musica.utils.Binding
import kotlinx.coroutines.awaitAll

object MediaPlayerManger : LifecycleObserver {
    var mediaPlayer: MediaPlayer? = null
    var currentPlayingSong: SongModel? = null
    val currentPlaying = MutableLiveData<SongModel>()
    val isPlaying = MutableLiveData<Boolean>()
    val currentProgress = MutableLiveData<Long>()
    var currentPlayList: List<SongModel>? = null
    private val handler = Handler(Looper.myLooper()!!)
    private val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                setProgress(mediaPlayer!!.currentPosition.toLong())
                handler.postDelayed(this, 10)
            }
        }
    }

    fun pauseAudio(context: Context) {
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
        if (currentPlayingSong != null)
            showNotification(
                currentPlayingSong!!,
                0,
                context
            )
        isPlaying.postValue(false)
        mediaPlayer = null
    }

    fun playAudio(context: Context, dist: Int) {
        currentPlaying.postValue(currentPlayingSong)
        val currentSongUri = currentPlayingSong!!.filePath
        val progress = if (dist == 0) currentProgress.value!! else 0
        startAudio(context, currentSongUri, progress.toInt())
        showNotification(
            currentPlayingSong!!,
            1,
            context
        )
    }

    private fun startAudio(context: Context, currentSongUri: String, progress: Int) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, Uri.parse(currentSongUri))
            prepare()
            seekTo(progress)
            start()
            setOnCompletionListener {
                pauseAudio(context)
                setProgress(0)
                onNextPreviousPress(context, 1, true)
            }
            pauseAudio(context)
            handler.postDelayed(runnable, 10)
        }
        isPlaying.postValue(true)
    }

    fun onNextPreviousPress(context: Context, value: Int, play: Boolean) {
        val position =
            (currentPlayList!!.indexOf(currentPlayingSong!!) + value).mod(
                currentPlayList!!.size
            )
        currentPlayingSong = (currentPlayList!![position])
        currentPlaying.postValue(currentPlayingSong)
        pauseAudio(context)
        setProgress(0)
        if (play) {
            playAudio(context, 1)
            showNotification(
                currentPlayingSong!!,
                1,
                context
            )
        } else {
            showNotification(
                currentPlayingSong!!,
                0,
                context
            )
        }

    }

    fun setProgress(value: Long) {
        currentProgress.postValue(value)
    }

    fun showNotification(song: SongModel, playPause: Int, baseContext: Context) {
        val mediaSession: MediaSessionCompat = MediaSessionCompat(
            baseContext,
            PlayService.MUSIC_SERVICE
        )
        val notificationManager = baseContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).apply {
            action = PlayService.ACTION_Previous
        }
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(PlayService.ACTION_PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val pauseIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(
            PlayService.ACTION_PAUSE
        )
        val pausePendingIntent = PendingIntent.getBroadcast(baseContext, 0, pauseIntent, flag)
        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(PlayService.ACTION_NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(PlayService.NOTIFICATION_CHANNEL_ID) == null
        ) {
            val name = baseContext.getString(R.string.app_name)
            val channel = NotificationChannel(
                PlayService.NOTIFICATION_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val songImage = Binding.getAlbumArt(song.filePath)
        val notificationImage =
            if (songImage != null) BitmapFactory.decodeByteArray(
                songImage,
                0,
                songImage.size
            )
            else BitmapFactory.decodeResource(
                baseContext.resources,
                R.drawable.back
            )
        val playPauseButton =
            if (playPause == 0) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24
        val playPauseAction =
            if (playPause == 0) PlayService.ACTION_PLAY else PlayService.ACTION_PAUSE
        val playPausePendingIntent = if (playPause == 0) playPendingIntent else pausePendingIntent
        val notification = NotificationCompat.Builder(
            baseContext,
            PlayService.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.lancher)
            .setContentTitle(song.title)
            .setContentText(song.filePath)
            .setLargeIcon(
                notificationImage
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(
                    mediaSession.sessionToken
                )
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(
                R.drawable.baseline_keyboard_double_arrow_left_24,
                PlayService.ACTION_Previous,
                prevPendingIntent
            )
            .addAction(
                playPauseButton, playPauseAction, playPausePendingIntent
            )
            .addAction(
                R.drawable.baseline_keyboard_double_arrow_right_24,
                PlayService.ACTION_NEXT, nextPendingIntent
            )
            .build()

        notificationManager.notify(PlayService.FOREGROUND_SERVICE_ID, notification)
    }
}