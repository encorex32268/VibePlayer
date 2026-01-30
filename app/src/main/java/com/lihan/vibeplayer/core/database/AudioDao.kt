package com.lihan.vibeplayer.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AudioDao{

    @Upsert
    suspend fun upsertAudio(audioEntity: AudioEntity)

    @Query("DELETE FROM AudioEntity WHERE id NOT IN (:ids)")
    suspend fun deleteAudiosByIds(ids: List<Int>)

    @Delete
    suspend fun deleteAudio(audioEntity: AudioEntity)

    @Query("SELECT * FROM audioentity")
    fun getAudios(): Flow<List<AudioEntity>>

    @Query("SELECT * FROM AudioEntity WHERE id = :audioId")
    fun getAudioById(audioId: Int): Flow<AudioEntity?>

    @Query("SELECT * FROM AudioEntity WHERE id IN(:ids)")
    fun getAudiosByIds(ids: List<Int>): Flow<List<AudioEntity>>

    @Query("UPDATE AudioEntity SET isFavourite = :isFavourite, addedFavouriteTimestamp = :timestamp WHERE id = :audioId")
    suspend fun updateFavouriteStatus(audioId: Int, isFavourite: Boolean,timestamp: Long?)

    @Transaction
    suspend fun upsertAudioList(audioEntities: List<AudioEntity>){

        val newAudioIds = audioEntities.mapNotNull { it.id}

        deleteAudiosByIds(newAudioIds)

        val favouriteIds = getFavouriteAudios().first().map { it.id }.toSet()

        val mergedAudios = audioEntities.map { audio ->
           audio.copy(isFavourite = audio.id in favouriteIds)
        }

        mergedAudios.forEach {
            upsertAudio(it)
        }
    }

    @Query("SELECT * FROM AudioEntity WHERE isFavourite = 1 ORDER BY addedFavouriteTimestamp ASC ")
    fun getFavouriteAudios(): Flow<List<AudioEntity>>

    @Query("SELECT COUNT(*) FROM AudioEntity WHERE isFavourite = 1")
    fun getFavouriteCount(): Flow<Int>

}