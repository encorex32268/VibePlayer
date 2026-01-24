package com.lihan.vibeplayer.music_list.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface MusicListRepository {

    fun getAllAudios(): Flow<List<Audio>>

    suspend fun getAudiosBySizeAndDuration(
        duration: Long,
        size: Long,
    ): List<Audio>

    suspend fun getAudiosByTitle(text: String): List<Audio>

    suspend fun getAlbumArtImage(uri: Uri): ByteArray?

    suspend fun upsertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun createFavouritePlaylist(favouritePlaylist: FavouritesPlaylist)

    suspend fun checkAndCreateDefaultPlaylist()

    fun getAllPlaylist(): Flow<List<Playlist>>

    fun getFavouritesPlaylist(): Flow<FavouritesPlaylist?>

}