package com.lihan.vibeplayer.music_list.presentation.model

import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText

enum class RepeatModeStatus {
    Off,All,One;

    fun toUiText(): UiText{
        return when(this){
            RepeatModeStatus.Off -> UiText.StringResource(R.string.main_repeat_mode_off)
            RepeatModeStatus.All -> UiText.StringResource(R.string.main_repeat_mode_all)
            RepeatModeStatus.One -> UiText.StringResource(R.string.main_repeat_mode_one)
        }
    }
}