package com.lihan.vibeplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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