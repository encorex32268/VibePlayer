package com.lihan.vibeplayer.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int?=null,
    val albumUri: String,
    val songTitle: String,
    val artisName: String,
    val duration: Long
)
