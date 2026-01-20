package com.lihan.vibeplayer.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun create(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun delete(playlistEntity: PlaylistEntity)

    @Query(
        """
            SELECT * From playlistentity
        """
    )
    fun getPlaylists(): Flow<List<PlaylistEntity>>
}