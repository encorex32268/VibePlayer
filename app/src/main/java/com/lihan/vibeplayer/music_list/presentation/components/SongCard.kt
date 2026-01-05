package com.lihan.vibeplayer.music_list.presentation.components

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SongCard(
    audioUi: AudioUi,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageByteArray by remember{
        mutableStateOf<ByteArray?>(null)
    }
    LaunchedEffect(audioUi.album) {
        if (audioUi.album != null){
            val data = withContext(Dispatchers.IO) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, audioUi.album)
                    retriever.embeddedPicture
                } catch (e: Exception) {
                    null
                } finally {
                    retriever.release()
                }
            }
            imageByteArray = data
        }
    }

    Row(
        modifier = modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageByteArray,
            contentDescription = audioUi.songTitle,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(64.dp),
            placeholder = painterResource(R.drawable.song_image_default)
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
                color = TextPrimary
            )
            Text(
                text = audioUi.artisName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Text(
            text = audioUi.duration.toTimeString(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }

}

fun Long.toTimeString(): String{
    val mins = this / 60 / 1000
    val seconds = (this / 1000) % 60
    return String.format(
        "%02d:%02d",mins,seconds
    )
}

@Preview
@Composable
private fun SongCardPreview() {
    VibePlayerTheme {
        SongCard(
            audioUi = AudioUi(
                songTitle = "505",
                artisName = "Arctic Monkeys",
                duration = 60_000
            )
        )
    }
}
