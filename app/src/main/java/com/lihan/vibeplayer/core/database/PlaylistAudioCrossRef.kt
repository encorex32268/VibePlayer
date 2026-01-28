package com.lihan.vibeplayer.core.database

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["playlistId", "audioId"], indices = [
        Index(value = ["audioId"])
    ]
)
data class PlaylistAudioCrossRef(
    val playlistId: Int,
    val audioId: Int
)