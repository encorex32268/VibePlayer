package com.lihan.vibeplayer.main.presentation.play

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun PlayingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ){
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.arrow_left),
                onClick = {
                    //TODO: Playing Back
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                modifier = Modifier.size(320.dp),
                imageVector = ImageVector.vectorResource(R.drawable.song_image_default),
                contentDescription = ""
            )
            Spacer(Modifier.height(20.dp))
            //TODO: Song Info
            Text(
                text = "505",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "Arctic Monkeys",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = { 0.3f },
            color = Color.White,
            trackColor = SurfaceBG,
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.skip_previous),
                onClick = {}
            )
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Color.White , shape = CircleShape),
                onClick = {}
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.play),
                    contentDescription = null
                )
            }
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.skip_next),
                onClick = {}
            )
        }
    }


}

@Preview
@Composable
private fun PlayingScreenPreview() {
    VibePlayerTheme {
        PlayingScreen()
    }
}