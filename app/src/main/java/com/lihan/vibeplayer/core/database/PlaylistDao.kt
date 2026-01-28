package com.lihan.vibeplayer.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsert(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun delete(playlistEntity: PlaylistEntity)

    @Query("SELECT * From playlistentity Where id=:id")
    fun getPlaylistById(id: Int): Flow<PlaylistEntity>

    @Query("SELECT * From playlistentity")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Transaction
    @Query("SELECT * FROM PlaylistEntity WHERE id = :playlistId")
    fun getPlaylistWithAudios(playlistId: Int): Flow<PlaylistAudios>

}