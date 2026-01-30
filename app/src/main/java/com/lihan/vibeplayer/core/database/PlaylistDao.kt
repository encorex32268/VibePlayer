package com.lihan.vibeplayer.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsert(playlistEntity: PlaylistEntity): Long

    @Delete
    suspend fun delete(playlistEntity: PlaylistEntity)

    @Query("SELECT * From playlistentity Where id=:id")
    fun getPlaylistById(id: Int?): Flow<PlaylistEntity?>

    @Query("SELECT * From playlistentity")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistAudioCrossRef)

    @Query("SELECT * From PlaylistEntity")
    fun getPlaylistAudios(): Flow<List<PlaylistAudios>>

    @Query("SELECT * From PlaylistEntity WHERE id=:id")
    fun getPlaylistAudiosById(id: Int?): Flow<PlaylistAudios?>


    @Transaction
    suspend fun createPlaylistWithAudios(id: Int?,title: String, coverUri: String? , audios: List<String>){

        val existingPlaylist = id?.let { getPlaylistById(it).firstOrNull() }
        val finalCoverUri = existingPlaylist?.coverImageUriString ?: coverUri

        val upsertId = upsert(
            playlistEntity = PlaylistEntity(
                id = id?:0,
                title = title,
                coverImageUriString = finalCoverUri
            )
        )
        val finalPlaylistId = if (upsertId == -1L) id ?: 0 else upsertId.toInt()

        audios.forEach { audioId ->
            insertCrossRef(
                crossRef = PlaylistAudioCrossRef(
                    playlistId = finalPlaylistId,
                    audioId = audioId.toInt()
                )
            )
        }

    }



}