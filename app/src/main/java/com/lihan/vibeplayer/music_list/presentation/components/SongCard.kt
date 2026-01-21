package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.toCoilUri
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.domain.util.toTimeString
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.design_system.buttons.VPRadioButton
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun SongCard(
    audioUi: AudioUi,
    onAudioClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = false,
    onSelect: ((AudioUi) -> Unit) ? = null,
) {
    Row(
        modifier = modifier
            .clickable{
                onAudioClick()
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(isSelectable){
            VPRadioButton(
                selected = audioUi.isSelected,
                onClick = {
                    onSelect?.invoke(audioUi)
                }
            )
            Spacer(Modifier.width(12.dp))
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(audioUi.albumImage)
                .diskCacheKey("album_${audioUi.id}")
                .memoryCacheKey("album_${audioUi.id}")
                .build(),
            contentDescription = audioUi.songTitle,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(64.dp),
            placeholder = painterResource(R.drawable.song_image_default),
            error = painterResource(R.drawable.song_image_default)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = audioUi.songTitle,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audioUi.artisName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text =  audioUi.duration.toTimeString(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }

}



@Preview
@Composable
private fun SongCardPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SongCard(
                audioUi = AudioUi(
                    id = 1,
                    album = android.net.Uri.EMPTY,
                    songTitle = "505",
                    artisName = "Arctic Monkeys",
                    duration = 60_000
                ),
                onAudioClick = {

                }
            )

            SongCard(
                audioUi = AudioUi(
                    id = 1,
                    album = android.net.Uri.EMPTY,
                    songTitle = "505",
                    artisName = "Arctic Monkeys",
                    duration = 60_000,
                    isSelected = true
                ),
                onAudioClick = {

                },
                isSelectable = true,
                onSelect = {

                }
            )
        }
    }
}
