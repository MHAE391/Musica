package com.m391.musica.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.m391.musica.services.MediaPlayerManger.isPlaying
import com.m391.musica.services.MediaPlayerManger.mediaPlayer
import com.m391.musica.services.MediaPlayerManger.pauseAudio
import com.m391.musica.services.MediaPlayerManger.showNotification

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        handleIntent(context, intent)
    }

    private fun handleIntent(context: Context?, intent: Intent?) {
        when (intent?.action) {
            PlayService.ACTION_PLAY -> {
                MediaPlayerManger.playAudio(context!!, 0)
                showNotification(MediaPlayerManger.currentPlayingSong!!, 1, context!!)

            }

            PlayService.ACTION_PAUSE -> {
                pauseAudio(context!!)
                showNotification(MediaPlayerManger.currentPlayingSong!!, 0, context!!)

            }

            PlayService.ACTION_NEXT -> {
                if (isPlaying.value!!) {
                    MediaPlayerManger.onNextPreviousPress(
                        context!!,
                        1, true
                    )
                    showNotification(MediaPlayerManger.currentPlayingSong!!, 1, context!!)

                } else {
                    MediaPlayerManger.onNextPreviousPress(
                        context!!,
                        1, false
                    )
                    showNotification(MediaPlayerManger.currentPlayingSong!!, 0, context!!)
                }
            }

            PlayService.ACTION_Previous -> {
                if (isPlaying.value!!) {
                    MediaPlayerManger.onNextPreviousPress(
                        context!!,
                        -1, true
                    )
                    showNotification(MediaPlayerManger.currentPlayingSong!!, 1, context!!)
                } else {
                    MediaPlayerManger.onNextPreviousPress(
                        context!!,
                        -1, false
                    )
                    showNotification(MediaPlayerManger.currentPlayingSong!!, 1, context!!)

                }
            }
        }
    }

}