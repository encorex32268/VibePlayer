package com.lihan.vibeplayer.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesPlaylistDao {

    @Upsert
    suspend fun create(favouritesPlaylistEntity: FavouritesPlaylistEntity)

    @Delete
    suspend fun delete(favouritesPlaylistEntity: FavouritesPlaylistEntity)

    @Query(
        """
            SELECT * From favouritesplaylistentity
        """
    )
    fun getFavouritesPlaylist(): Flow<FavouritesPlaylistEntity?>
}