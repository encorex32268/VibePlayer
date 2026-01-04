package com.lihan.vibeplayer.main.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.main.presentation.model.SongUi
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun SongCard(
    songUi: SongUi,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.song_image_default),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = songUi.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = songUi.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Text(
            text = songUi.songTimestamp.toTimeString(),
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
            songUi = SongUi(
                title = "505",
                description = "Arctic Monkeys",
                songTimestamp = 60000
            )
        )
    }
}