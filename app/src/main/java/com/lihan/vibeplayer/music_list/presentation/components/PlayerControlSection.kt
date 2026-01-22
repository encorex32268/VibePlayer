package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus

@Composable
fun PlayerControlSection(
    isPlaying: Boolean,
    isEnabledShuffle: Boolean,
    repeatModeStatus: RepeatModeStatus,
    onPlayClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        CircleIconButton(
            icon = ImageVector.vectorResource(R.drawable.shuffle),
            onClick = onShuffleClick,
            isDisabledStyle = !isEnabledShuffle
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.skip_previous),
                onClick = onSkipPreviousClick
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clickable{
                        onPlayClick()
                    }
                    .clip(CircleShape)
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying){
                        ImageVector.vectorResource(R.drawable.pause)
                    }else{
                        ImageVector.vectorResource(R.drawable.play)
                    },
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.skip_next),
                onClick = onSkipNextClick
            )
        }
        CircleIconButton(
            icon = when(repeatModeStatus){
                RepeatModeStatus.Off ->  ImageVector.vectorResource(R.drawable.repeat_off)
                RepeatModeStatus.One -> ImageVector.vectorResource(R.drawable.repeat_one)
                RepeatModeStatus.All -> ImageVector.vectorResource(R.drawable.repeat)
            } ,
            onClick = onRepeatClick,
            isDisabledStyle = repeatModeStatus == RepeatModeStatus.Off
        )
    }
}
