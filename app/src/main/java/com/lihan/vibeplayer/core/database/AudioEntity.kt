package com.lihan.vibeplayer.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int?=null,
    val albumUri: String,
    val songTitle: String,
    val artisName: String,
    val duration: Long,
    val size: Long,
    val isFavourite: Boolean = false
)
