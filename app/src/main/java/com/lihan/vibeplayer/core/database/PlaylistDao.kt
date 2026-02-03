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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistAudioEntityCrossRef)

    @Query("SELECT * From PlaylistEntity" )
    fun getPlaylistAudios(): Flow<List<PlaylistAudios>>

    @Query("SELECT * From PlaylistEntity WHERE id=:id")
    fun getPlaylistAudiosById(id: Int?): Flow<PlaylistAudios?>

    @Query("DELETE FROM PlaylistAudioEntityCrossRef WHERE playlistId = :playlistId")
    suspend fun deleteCrossRefsByPlaylistId(playlistId: Int)

    @Transaction
    suspend fun createPlaylistWithAudios(id: Int?,title: String, coverUri: String? , audios: List<String>){

        val existingPlaylist = id?.let { getPlaylistById(it).firstOrNull() }
        val finalCoverUri = existingPlaylist?.coverImageUriString ?: coverUri

        val upsertId = upsert(
            playlistEntity = PlaylistEntity(
                id = id,
                title = title,
                coverImageUriString = finalCoverUri
            )
        )
        val finalPlaylistId = if (upsertId == -1L) id ?: 0 else upsertId.toInt()

        //before upsert need clear old data
        //make sure the order is correct
        if (id != null) {
            deleteCrossRefsByPlaylistId(id)
        }

        audios.forEachIndexed { index,audioId ->
            insertCrossRef(
                crossRef = PlaylistAudioEntityCrossRef(
                    playlistId = finalPlaylistId,
                    audioId = audioId.toInt(),
                    order = index
                )
            )
        }

    }



}