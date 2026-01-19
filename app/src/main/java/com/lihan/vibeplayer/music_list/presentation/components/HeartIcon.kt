package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.size.Size
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.theme.ButtonPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlin.math.roundToInt

@Composable
fun HeartIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ButtonPrimary,
                        ButtonPrimary.copy(alpha = 0.2f)
                    ),
                ), alpha = 0.14f
            )
            .size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(36.dp)
                .drawWithContent{
                    drawContent()
                    drawRect(
                        size = androidx.compose.ui.geometry.Size(
                            width = (size.width/2f),
                            height = size.height
                        ),
                        color = ButtonPrimary.copy(alpha = 0.5f)
                    )
                },
            imageVector =  ImageVector.vectorResource(R.drawable.heart),
            contentDescription = null,
            tint = Color.Unspecified
        )

    }

}


@Preview
@Composable
private fun PlaylistGradientIconPreview() {
    VibePlayerTheme {
        HeartIcon(

        )
    }
}
