package com.lihan.vibeplayer.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    version = 1,
    entities = [AudioEntity::class, PlaylistEntity::class,FavouritesPlaylistEntity::class],
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
    abstract val favouritesPlaylistDao: FavouritesPlaylistDao
}