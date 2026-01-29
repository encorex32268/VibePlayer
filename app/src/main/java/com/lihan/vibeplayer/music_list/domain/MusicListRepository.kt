package com.lihan.vibeplayer.music_list.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface MusicListRepository {

    suspend fun upsertAudio(audio: Audio)

    fun getAudioById(audioId: Int): Flow<Audio?>

    suspend fun updateFavouriteStatus(audioId: Int, isFavourite: Boolean)

    fun getAllAudiosAndSync(): Flow<List<Audio>>

    fun getAllAudios(): Flow<List<Audio>>

    fun getAudiosByIds(ids: List<Int>): Flow<List<Audio>>

    suspend fun getAudiosBySizeAndDuration(
        duration: Long,
        size: Long,
    ): List<Audio>

    suspend fun getAudiosByTitle(text: String): List<Audio>

    fun getFavouriteAudios(): Flow<List<Audio>>

    fun getFavouriteCount(): Flow<Int>

    suspend fun getAlbumArtImage(uri: Uri): ByteArray?


    suspend fun upsertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylist(): Flow<List<Playlist>>

    fun getPlaylistById(id: Int?): Flow<Playlist?>

    fun getPlaylistAudios(): Flow<List<PlaylistAudios>>

    fun getPlaylistAudiosById(id: Int?): Flow<PlaylistAudios?>

    suspend fun createPlaylistWithAudios(id: Int?,title: String, coverUri: String? , audios: List<String>)
}