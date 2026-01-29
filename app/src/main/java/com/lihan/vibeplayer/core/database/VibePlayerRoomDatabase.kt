package com.lihan.vibeplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 1,
    entities = [
        AudioEntity::class,
        PlaylistEntity::class,
        PlaylistAudioCrossRef::class
    ],
    exportSchema = false
)
@TypeConverters(
    value = [
        StringListConverter::class
    ]
)
abstract class VibePlayerRoomDatabase: RoomDatabase() {
    abstract val audioDao: AudioDao
    abstract val playlistDao: PlaylistDao
}