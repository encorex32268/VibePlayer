package com.lihan.vibeplayer.music_list.presentation.scan

data class ScanMusicState(
    val isScanning: Boolean = false,
    val secondSelectItems: List<String> = listOf("30s","60s"),
    val secondSelect: String = secondSelectItems.first(),
    val sizeSelectItems: List<String> = listOf("100KB","500KB"),
    val sizeSelect: String = sizeSelectItems.first(),
)




