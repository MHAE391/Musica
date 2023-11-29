package com.m391.musica.services

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.m391.musica.R
import com.m391.musica.models.SongModel
import com.m391.musica.utils.Statics.SONG_ALBUM
import com.m391.musica.utils.Statics.SONG_ARTIST
import com.m391.musica.utils.Statics.SONG_DURATION
import com.m391.musica.utils.Statics.SONG_FILE_PATH
import com.m391.musica.utils.Statics.SONG_ID
import com.m391.musica.utils.Statics.SONG_TITLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongService {

    @SuppressLint("NewApi", "Recycle")
    suspend fun getAllSongs(context: Context): List<SongModel> = withContext(Dispatchers.IO) {
        val songsList = mutableListOf<SongModel>()
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            SONG_ID,
            SONG_ARTIST,
            SONG_ALBUM,
            SONG_DURATION,
            SONG_TITLE,
            SONG_FILE_PATH
        )
        val songCursor = context.contentResolver.query(
            songUri,
            projection,
            null,
            null,
            null,
            null,

            )
        if (songCursor != null && songCursor.moveToFirst()) {
            val idColumn = songCursor.getColumnIndexOrThrow(SONG_ID)
            val titleColumn = songCursor.getColumnIndexOrThrow(SONG_TITLE)
            val artistColumn = songCursor.getColumnIndexOrThrow(SONG_ARTIST)
            val albumColumn = songCursor.getColumnIndexOrThrow(SONG_ALBUM)
            val durationColumn = songCursor.getColumnIndexOrThrow(SONG_DURATION)
            val filePathColumn = songCursor.getColumnIndexOrThrow(SONG_FILE_PATH)
            do {
                val id = songCursor.getLong(idColumn)
                val title = songCursor.getString(titleColumn)
                val artist = songCursor.getString(artistColumn)
                val album = songCursor.getString(albumColumn)
                val duration = songCursor.getLong(durationColumn)
                val filePath = songCursor.getString(filePathColumn)
                val song =
                    SongModel(
                        id,
                        title,
                        artist,
                        album,
                        duration,
                        filePath
                    )
                songsList.add(song)
            } while (songCursor.moveToNext())
            return@withContext songsList
        }

        return@withContext songsList
    }
}