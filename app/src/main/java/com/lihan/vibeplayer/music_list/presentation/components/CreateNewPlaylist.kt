@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.design_system.buttons.VPButton
import com.lihan.vibeplayer.ui.design_system.buttons.VPOutlineButton
import com.lihan.vibeplayer.ui.theme.ButtonHover28
import com.lihan.vibeplayer.ui.theme.SurfaceHighest
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun CreateNewPlaylist(
    textFieldState: TextFieldState,
    onCreateClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCreateButtonEnabled: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    val textCount = remember(textFieldState.text.toString()){
        textFieldState.text.toString().length
    }

    ModalBottomSheet(
        onDismissRequest = {
            focusManager.clearFocus()
            keyboard?.hide()
            onCancelClick()
        },
        modifier = modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .padding(16.dp),
        containerColor = SurfaceHighest,
        dragHandle = null
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            text = stringResource(R.string.playlist_create_new_playlist),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        BasicTextField(
            state = textFieldState,
            decorator = { inner ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ){
                        if (textFieldState.text.toString().isEmpty()){
                            Text(
                                text = stringResource(R.string.playlist_bottom_sheet_place_holder),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                color = TextSecondary
                            )
                        }
                        inner()
                    }
                    Text(
                        text = stringResource(R.string.playlist_bottom_sheet_place_holder_count,textCount),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary
                    )
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                color = TextPrimary
            ),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(100))
                .border(
                    width = 1.dp,
                    color = ButtonHover28,
                    shape = RoundedCornerShape(100)
                )
                .background(
                    color = ButtonHover28,
                    shape = RoundedCornerShape(100)
                )
                .padding(
                    vertical = 12.dp,
                    horizontal = 16.dp
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VPOutlineButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    onCancelClick()
                }
            )
            VPButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.create),
                enabled = isCreateButtonEnabled,
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    onCreateClick()
                }
            )
        }
    }


}


@Preview
@Composable
private fun CreateNewPlaylistPreview() {
    VibePlayerTheme {
        CreateNewPlaylist(
            textFieldState = TextFieldState(initialText = "rrr"),
            onCreateClick = {},
            onCancelClick = {},
            isCreateButtonEnabled = false
        )
    }
}