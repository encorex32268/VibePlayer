package com.lihan.vibeplayer.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["playlistId", "audioId"],
    indices = [Index(value = ["audioId"])],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AudioEntity::class,
            parentColumns = ["id"],
            childColumns = ["audioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistAudioCrossRef(
    val playlistId: Int,
    val audioId: Int
)