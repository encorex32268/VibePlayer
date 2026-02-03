package com.lihan.vibeplayer.music_list.presentation.model


data class PlaylistAudiosUi(
    val playlist: PlaylistUi,
    val audios: List<AudioUi>,
    val crossRefs: List<PlaylistAudioUiCrossRef>
){
    val sortedAudios: List<AudioUi>
        get() {
            val orderMap = crossRefs.associate { it.audioId to it.order }
            return audios.sortedBy { orderMap[it.id.toInt()] ?: Int.MAX_VALUE }
        }
}
