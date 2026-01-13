package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.design_system.buttons.VPOutlineButton
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun ListFunctionSection(
    isTablet: Boolean,
    songListSize: Int,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val commonModifier = modifier.fillMaxWidth()

    if (isTablet) {
        Row(
            modifier = commonModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionButtons(
                onShuffleClick = onShuffleClick,
                onPlayClick = onPlayClick,
                useWeight = false
            )
            Text(
                text = stringResource(R.string.main_list_function_songs_size, songListSize),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary
            )
        }
    } else {
        Column(
            modifier = commonModifier
        ) {
            FunctionButtons(
                onShuffleClick = onShuffleClick,
                onPlayClick = onPlayClick,
                useWeight = true
            )
            Text(
                text = stringResource(R.string.main_list_function_songs_size, songListSize),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun FunctionButtons(
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    useWeight: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VPOutlineButton(
            modifier = if (useWeight) Modifier.fillMaxWidth().weight(1f) else Modifier,
            text = stringResource(R.string.main_list_function_shuffle),
            onClick = onShuffleClick,
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.repeat),
                    contentDescription = stringResource(R.string.main_list_function_shuffle),
                    tint = TextPrimary
                )
            }
        )
        VPOutlineButton(
            modifier = if (useWeight) Modifier.fillMaxWidth().weight(1f) else Modifier,
            text = stringResource(R.string.main_list_function_play),
            onClick = onPlayClick,
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.play_outline),
                    contentDescription = stringResource(R.string.main_list_function_play),
                    tint = TextPrimary
                )
            }
        )
    }
}



@Preview
@Composable
private fun ListFunctionSectionPreview() {
    VibePlayerTheme {
        ListFunctionSection(
            onPlayClick = {},
            onShuffleClick = {},
            songListSize = 123,
            isTablet = false
        )
    }
}