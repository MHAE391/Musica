package com.m391.musica.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.PlaybackState.ACTION_PAUSE
import android.media.session.PlaybackState.ACTION_PLAY
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.m391.musica.MainActivity
import com.m391.musica.R
import com.m391.musica.models.SongModel

class PlayService : Service() {
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): PlayService = this@PlayService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, PlayService::class.java)
        intent.action = action
        return PendingIntent.getService(this, 0, intent, 0)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_PLAY -> {

            }

            ACTION_PAUSE -> {

            }

            ACTION_NEXT -> {

            }

            ACTION_Previous -> {

            }
        }
    }

    fun showNotification() {
        val notification = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setContentTitle("")
    }

    companion object {
        private const val CHANNEL_ID = "music_channel"
        private const val FOREGROUND_SERVICE_ID = 123
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_Previous = "action_previous"

    }
}