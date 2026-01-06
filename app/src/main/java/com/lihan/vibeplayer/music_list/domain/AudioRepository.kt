package com.lihan.vibeplayer.music_list.domain

interface AudioRepository {
    fun getAudios(
        duration: Long = 0L,
        size: Long = 0L
    ): List<Audio>

    suspend fun getAlbumArt(albumUri: android.net.Uri): ByteArray?
}