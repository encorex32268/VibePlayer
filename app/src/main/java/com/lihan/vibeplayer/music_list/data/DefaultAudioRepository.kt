package com.lihan.vibeplayer.music_list.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import com.lihan.vibeplayer.core.domain.LocalDataRepository
import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class DefaultAudioRepository(
    private val context: Context,
    private val localDataRepository: LocalDataRepository
): AudioRepository{

    override fun getAllAudios(): List<Audio>{
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        return getDeviceAudiosByQuery(selection,emptyArray())

    }

    override fun getAllAudiosFlow(): Flow<List<Audio>>{
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val deviceAudios = getDeviceAudiosByQuery(selection,emptyArray())
        return localDataRepository.getAudios()
            .onStart {
                localDataRepository.upsertAudios(deviceAudios)
            }
    }

    override fun getAudiosBySizeAndDuration(
        duration: Long,
        size: Long,
    ): List<Audio> {
        val selection =  "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " +
                "${MediaStore.Audio.Media.DURATION} <= ? AND " +
                "${MediaStore.Audio.Media.SIZE} <= ? "

        val selectionArgs = arrayOf("$duration","$size")

        return getDeviceAudiosByQuery(selection,selectionArgs)
    }

    override fun getAudiosByTitle(text: String): List<Audio> {
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " +
                "${MediaStore.Audio.Media.TITLE} LIKE ?"
        val selectionArgs = arrayOf("%$text%")

        return getDeviceAudiosByQuery(selection,selectionArgs)

    }



    override suspend fun getAlbumArt(albumUri: android.net.Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, albumUri)
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



    private fun getDeviceAudiosByQuery(
        selection: String,
        selectionArgs: Array<String>
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


            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getLong(durationColumn)

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
                    )
                )
            }
        }
        return audios
    }
}