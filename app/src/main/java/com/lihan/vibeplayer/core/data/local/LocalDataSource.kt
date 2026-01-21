package com.lihan.vibeplayer.core.data.local

import com.lihan.vibeplayer.core.data.mapper.toData
import com.lihan.vibeplayer.core.data.mapper.toDomain
import com.lihan.vibeplayer.core.domain.LocalDataRepository
import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.domain.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataSource(
    private val db: VibePlayerRoomDatabase
): LocalDataRepository{

    override suspend fun upsertAudios(audios: List<Audio>) {
        db.audioDao.upsertAudioList(
            audios.map { it.toData() }
        )
    }

    override suspend fun deleteAudio(audio: Audio) {
        db.audioDao.deleteAudio(audio.toData())
    }

    override fun getAudios(): Flow<List<Audio>> {
       return db.audioDao.getAudios().map {
           it.map { audioEntity ->
               audioEntity.toDomain()
           }
       }
    }

    override suspend fun createPlaylist(playlist: Playlist) {
        db.playlistDao.create(playlist.toData())
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        db.playlistDao.delete(playlist.toData())
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return db.playlistDao.getPlaylists().map {
            it.map { playlistEntity ->
                playlistEntity.toDomain()
            }
        }
    }

    override suspend fun createFavouritesPlaylist(favouritesPlaylist: FavouritesPlaylist) {
        db.favouritesPlaylistDao.create(favouritesPlaylist.toData())
    }

    override suspend fun deleteFavouritesPlaylist(favouritesPlaylist: FavouritesPlaylist) {
        db.favouritesPlaylistDao.delete(favouritesPlaylist.toData())
    }

    override fun getFavouritesPlaylists(): Flow<FavouritesPlaylist?> {
        return db.favouritesPlaylistDao.getFavouritesPlaylist().map { it?.toDomain() }
    }
}