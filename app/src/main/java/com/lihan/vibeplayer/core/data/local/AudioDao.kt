package com.lihan.vibeplayer.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao{

    @Upsert
    suspend fun upsertAudio(audioEntity: AudioEntity)

    @Delete
    suspend fun deleteAudio(audioEntity: AudioEntity)

    @Query("""
        SELECT * FROM audioentity
    """)
    fun getAudios(): Flow<List<AudioEntity>>


    @Transaction
    suspend fun upsertAudioList(audioEntities: List<AudioEntity>){
        audioEntities.forEach {
            upsertAudio(it)
        }
    }

}