package com.lihan.vibeplayer.music_list.domain

import android.net.Uri
import com.lihan.vibeplayer.core.database.PlaylistAudios
import kotlinx.coroutines.flow.Flow

interface MusicListRepository {

    fun getAllAudiosAndSync(): Flow<List<Audio>>

    fun getAllAudios(): Flow<List<Audio>>

    fun getAudiosByIds(ids: List<Int>): Flow<List<Audio>>

    suspend fun getAudiosBySizeAndDuration(
        duration: Long,
        size: Long,
    ): List<Audio>

    suspend fun getAudiosByTitle(text: String): List<Audio>

    fun getFavouriteAudios(): Flow<List<Audio>>

    suspend fun getAlbumArtImage(uri: Uri): ByteArray?

    suspend fun updateFavouriteStatus(audioId: Long, isFavourite: Boolean)


    suspend fun upsertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylist(): Flow<List<Playlist>>

    fun getPlaylistById(id: Int): Flow<Playlist>


    fun getPlaylistWithAudios(playlistId: Int): Flow<PlaylistAudios>

}