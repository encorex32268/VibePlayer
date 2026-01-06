package com.lihan.vibeplayer.music_list.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class DefaultAudioRepository(
    private val context: Context
): AudioRepository{

    override fun getAudios(
        duration: Long,
        size: Long
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
        val newSelection = if (duration == 0L && size == 0L){
            "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        }else{
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " +
                    "${MediaStore.Audio.Media.DURATION} <= ? AND " +
                    "${MediaStore.Audio.Media.SIZE} <= ? "
        }
        val newSelectionArgs = if (duration == 0L && size == 0L){
            emptyArray<String>()
        }else{
            arrayOf("$duration","$size")
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val audios = mutableListOf<Audio>()

        contentResolver.query(
            collection,
            projection,
            newSelection,
            newSelectionArgs,
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
}