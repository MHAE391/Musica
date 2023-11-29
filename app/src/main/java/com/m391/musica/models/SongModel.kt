package com.m391.musica.models

import android.util.Log
import java.text.FieldPosition

data class SongModel(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val filePath: String
)
