package com.lihan.vibeplayer.music_list.presentation.model

import android.net.Uri

data class AudioUi(
    val id: Long,
    val album: Uri,
    val songTitle: String,
    val artisName: String,
    val duration: Long,
    val isSelected: Boolean = false,
    val albumImage: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioUi

        if (id != other.id) return false
        if (album != other.album) return false
        if (songTitle != other.songTitle) return false
        if (artisName != other.artisName) return false
        if (duration != other.duration) return false
        if (isSelected != other.isSelected) return false
        if (albumImage != null) {
            if (other.albumImage == null) return false
            if (!albumImage.contentEquals(other.albumImage)) return false
        } else if (other.albumImage != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + songTitle.hashCode()
        result = 31 * result + artisName.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + (albumImage?.contentHashCode() ?: 0)
        return result
    }
}
