package com.lihan.vibeplayer.music_list.domain

interface AudioRepository {
    suspend fun getAudios(): List<Audio>
}