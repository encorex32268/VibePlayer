package com.lihan.vibeplayer.core.domain

import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.domain.Playlist
import kotlinx.coroutines.flow.Flow

interface LocalDataRepository {

    suspend fun upsertAudios(audios: List<Audio>)
    suspend fun deleteAudio(audio: Audio)
    fun getAudios(): Flow<List<Audio>>

    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun createFavouritesPlaylist(favouritesPlaylist: FavouritesPlaylist)
    suspend fun deleteFavouritesPlaylist(favouritesPlaylist: FavouritesPlaylist)
    fun getFavouritesPlaylists(): Flow<FavouritesPlaylist?>
}