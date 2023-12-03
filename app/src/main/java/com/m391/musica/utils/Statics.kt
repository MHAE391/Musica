package com.m391.musica.utils

import android.provider.MediaStore

object Statics {

    const val SONG_ID = MediaStore.Audio.Media._ID
    const val SONG_ARTIST = MediaStore.Audio.Media.ARTIST
    const val SONG_ALBUM = MediaStore.Audio.Media.ALBUM
    const val SONG_DURATION = MediaStore.Audio.Media.DURATION
    const val SONG_FILE_PATH = MediaStore.Audio.Media.DATA
    const val SONG_TITLE = MediaStore.Audio.Media.TITLE
    const val ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID

    //////////////////
    const val NOTIFICATION_CHANNEL_ID = "com.m391.musica.channel"
    const val FOREGROUND_SERVICE_ID = 123
    const val ACTION_PLAY = "action_play"
    const val ACTION_PAUSE = "action_pause"
    const val ACTION_NEXT = "action_next"
    const val ACTION_CANCEL = "action_cancel"
    const val ACTION_PREVIOUS = "action_previous"
    const val MUSIC_SERVICE = "music_service"
    const val CURRENT_PLAYLIST = "current_playlist"
    const val CURRENT_PLAYING_SONG = "current_playing_song"
}