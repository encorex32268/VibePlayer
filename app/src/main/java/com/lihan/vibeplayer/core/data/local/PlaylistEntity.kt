package com.lihan.vibeplayer.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int?=null,
    val title: String,
    val audioIds: List<String>
)
