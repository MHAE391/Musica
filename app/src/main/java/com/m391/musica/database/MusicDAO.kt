package com.m391.musica.database


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MusicDAO {
    @Insert
    suspend fun insert(music: Music)

    @Delete
    suspend fun delete(music: Music)

    @Query("SELECT * FROM musics")
    suspend fun getAll(): List<Music>

    @Query("SELECT * FROM musics WHERE id = :musicId")
    suspend fun getSongById(musicId: Long): Music?

    @Query("DELETE FROM musics")
    suspend fun deleteAll()


}