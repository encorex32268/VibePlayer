@file:OptIn(ExperimentalUuidApi::class)

package com.lihan.vibeplayer.music_list.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.lihan.vibeplayer.core.database.VibePlayerRoomDatabase
import com.lihan.vibeplayer.music_list.data.mapper.toData
import com.lihan.vibeplayer.music_list.data.mapper.toDomain
import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.domain.PlaylistAudios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi

class OfflineMusicListRepository(
    private val context: Context,
    private val db: VibePlayerRoomDatabase
): MusicListRepository{

    override suspend fun upsertAudio(audio: Audio) {
        db.audioDao.upsertAudio(audio.toData())
    }

    override fun getAudioById(audioId: Int): Flow<Audio?> {
        return db.audioDao.getAudioById(audioId).map { it?.toDomain() }
    }

    override suspend fun updateFavouriteStatus(audioId: Int, isFavourite: Boolean , timestamp: Long?) {
        db.audioDao.updateFavouriteStatus(audioId,isFavourite, timestamp)
    }

    override fun getAllAudiosAndSync(): Flow<List<Audio>> {
        return db.audioDao.getAudios().onStart {
            val audios = getDeviceAudiosByQuery(selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0")
            db.audioDao.upsertAudioList(audios.map { it.toData() })
        }.map {
            it.map { audioEntity ->
                audioEntity.toDomain()
            }
        }
    }

    override fun getAllAudios(): Flow<List<Audio>> {
        return db.audioDao.getAudios().map { it.map { audioEntity -> audioEntity.toDomain() } }
    }

    override fun getAudiosByIds(ids: List<Int>): Flow<List<Audio>> {
        return db.audioDao.getAudiosByIds(ids).map { it.map { value -> value.toDomain() } }
    }

    override suspend fun getAudiosBySizeAndDuration(
        duration: Long,
        size: Long
    ): List<Audio> {
        val audios = db.audioDao.getAudios().firstOrNull()?:emptyList()
        return if (audios.isEmpty()){
            emptyList()
        }else{
            audios.filter { audioEntity ->
                audioEntity.duration <= duration && audioEntity.size <= size
            }.map { it.toDomain() }
        }
    }

    override suspend fun getAudiosByTitle(text: String): List<Audio> {
        val audios = db.audioDao.getAudios().firstOrNull()?:emptyList()
        return if (audios.isEmpty()){
            emptyList()
        }else{
            audios.filter { audioEntity ->
                audioEntity.songTitle.contains(text)
            }.map { it.toDomain() }
        }
    }

    override fun getFavouriteAudios(): Flow<List<Audio>> {
        return db.audioDao.getFavouriteAudios().map { it.map { audioEntity -> audioEntity.toDomain() } }
    }

    override fun getFavouriteCount(): Flow<Int> {
        return db.audioDao.getFavouriteCount()
    }

    override suspend fun getAlbumArtImage(uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                retriever.embeddedPicture
            } catch (e: Exception) {
                ensureActive()
                e.printStackTrace()
                null
            } finally {
                retriever.release()
            }
        }
    }

    override suspend fun upsertPlaylist(playlist: Playlist): Long {
        return db.playlistDao.upsert(
            playlist.toData()
        )
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        db.playlistDao.delete(
            playlist.toData()
        )
    }


    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return db.playlistDao.getPlaylists().map { it.map { playlistEntity ->
            playlistEntity.toDomain()
        } }
    }

    override fun getPlaylistById(id: Int?): Flow<Playlist?> {
        return db.playlistDao.getPlaylistById(id).map { it?.toDomain() }
    }

    override fun getPlaylistAudios(): Flow<List<PlaylistAudios>> {
        return db.playlistDao
            .getPlaylistAudios()
            .map { it.map {  dbPlaylistAudios -> dbPlaylistAudios.toDomain() }}

    }

    override fun getPlaylistAudiosById(id: Int?): Flow<PlaylistAudios?> {
        return db.playlistDao.getPlaylistAudiosById(id).map { it?.toDomain() }
    }

    override suspend fun createPlaylistWithAudios(
        id: Int?,
        title: String,
        coverUri: String?,
        audios: List<String>
    ) {
        db.playlistDao.createPlaylistWithAudios(
            id = id,
            title = title,
            coverUri = coverUri,
            audios = audios
        )
    }


    private fun getDeviceAudiosByQuery(
        selection: String,
        selectionArgs: Array<String> = arrayOf()
    ): List<Audio> {

        val contentResolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE
        )

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val audios = mutableListOf<Audio>()

        contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getLong(sizeColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                audios.add(
                    Audio(
                        id = id,
                        album = contentUri,
                        songTitle = title,
                        artisName = artist,
                        duration = duration,
                        size = size
                    )
                )
            }
        }

        return audios
    }
}




















