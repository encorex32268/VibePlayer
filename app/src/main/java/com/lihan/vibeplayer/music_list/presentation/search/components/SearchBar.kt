package com.lihan.vibeplayer.music_list.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.design_system.buttons.VPTextButton
import com.lihan.vibeplayer.ui.theme.ButtonHover
import com.lihan.vibeplayer.ui.theme.ButtonHover28
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    textFieldState: TextFieldState,
    onCancelClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        //wait for ui building
        delay(300L)
        focusRequester.requestFocus()
    }

    val textSelectionColors = TextSelectionColors(
        handleColor = TextSecondary,
        backgroundColor = TextSecondary
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            BasicTextField(
                state = textFieldState,
                decorator = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            contentDescription = stringResource(R.string.search),
                            tint = TextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ){
                            if(textFieldState.text.toString().isEmpty()){
                                Text(
                                    text = stringResource(R.string.search),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSecondary
                                )
                            }
                            innerTextField()
                        }
                        if (textFieldState.text.toString().isNotEmpty()){
                            IconButton(
                                modifier = Modifier.size(16.dp),
                                onClick = {
                                    onCloseClick()
                                }
                            ){
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.search),
                                    tint = TextDisabled
                                )
                            }
                        }
                    }

                },
                textStyle =  MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(TextSecondary),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .border(
                        width = 1.dp,
                        color = SurfaceOutline,
                        shape = RoundedCornerShape(100)
                    )
                    .background(
                        color = ButtonHover28,
                        shape = RoundedCornerShape(100)
                    )
                    .weight(1f)
                    .padding(
                        vertical = 12.dp,
                        horizontal = 16.dp
                    )

            )
        }

        VPTextButton(
            text = stringResource(R.string.search_cancel),
            onClick = onCancelClick
        )
    }

}


@Preview
@Composable
private fun SearchBarPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SearchBar(
                textFieldState = TextFieldState(),
                onCancelClick = {},
                onCloseClick = {}
            )

            SearchBar(
                textFieldState = TextFieldState(
                    initialText = "Les"
                ),
                onCancelClick = {},
                onCloseClick = {}
            )
        }

    }
}