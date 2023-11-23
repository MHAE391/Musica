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
}