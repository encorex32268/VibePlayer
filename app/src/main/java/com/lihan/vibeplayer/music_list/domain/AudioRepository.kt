package com.lihan.vibeplayer.music_list.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    fun getAllAudios(): List<Audio>


    fun getAllAudiosFlow(): Flow<List<Audio>>

    fun getAudiosBySizeAndDuration(
        duration: Long,
        size: Long,
    ): List<Audio>

    fun getAudiosByTitle(text: String): List<Audio>

    suspend fun getAlbumArt(albumUri: Uri): ByteArray?
}