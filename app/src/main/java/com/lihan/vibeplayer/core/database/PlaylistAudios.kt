package com.lihan.vibeplayer.core.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistAudios(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistAudioCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "audioId"
        )
    )
    val audios: List<AudioEntity>
)