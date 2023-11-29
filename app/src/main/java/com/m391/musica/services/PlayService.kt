package com.m391.musica.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.m391.musica.R
import com.m391.musica.models.SongModel
import com.m391.musica.utils.Binding.getAlbumArt

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


    companion object {
        const val NOTIFICATION_CHANNEL_ID = "com.m391.musica.channel"
        const val FOREGROUND_SERVICE_ID = 123
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_Previous = "action_previous"
        const val MUSIC_SERVICE = "music_service"

    }
}