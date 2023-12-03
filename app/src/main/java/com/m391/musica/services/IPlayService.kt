package com.m391.musica.services

import com.m391.musica.models.SongModel

interface IPlayService {
    fun play()
    fun pause()
    fun nextPrevious(value: Int)
}