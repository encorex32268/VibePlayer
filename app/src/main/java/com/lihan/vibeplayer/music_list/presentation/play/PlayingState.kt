package com.lihan.vibeplayer.music_list.presentation.play

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

data class PlayingState(
    val audios: List<AudioUi> = emptyList(),
    val currentAudio: AudioUi?=null,
    val isPlaying: Boolean = false,
    val imageByteArray: ByteArray?=null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayingState

        if (isPlaying != other.isPlaying) return false
        if (currentAudio != other.currentAudio) return false
        if (!imageByteArray.contentEquals(other.imageByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isPlaying.hashCode()
        result = 31 * result + (currentAudio?.hashCode() ?: 0)
        result = 31 * result + (imageByteArray?.contentHashCode() ?: 0)
        return result
    }
}
