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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.theme.ButtonPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary

@Composable
fun PlaylistCardIcon(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    color: Color?=null,
    brush: Brush?=null,
    alpha: Float = 0f,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .then(
                when{
                    color != null -> {
                        Modifier.background(color = color.copy(alpha = alpha))
                    }
                    brush != null -> {
                        Modifier.background(brush = brush,alpha = alpha)
                    }
                    else -> Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }

}

@Composable
fun CardFavouriteIcon(
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp,
    backgroundSize: Dp = 64.dp,
) {
    PlaylistCardIcon(
        modifier = modifier
            .size(backgroundSize),
        brush = Brush.linearGradient(
            colors = listOf(
                ButtonPrimary,
                ButtonPrimary.copy(alpha = 0.2f)
            ),
        ),
        alpha = 0.14f,
        icon = {
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent{
                        drawContent()

                        drawArc(
                            startAngle = -90f,
                            sweepAngle = 180f,
                            color =  ButtonPrimary.copy(alpha = 0.5f),
                            useCenter = true,
                            blendMode = BlendMode.SrcAtop,
                        )

                        drawArc(
                            startAngle = 90f,
                            sweepAngle = 180f,
                            color =  ButtonPrimary,
                            useCenter = true,
                            blendMode = BlendMode.SrcAtop,
                        )

                    },
                imageVector =  ImageVector.vectorResource(R.drawable.heart),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    )
}



@Composable
fun CardNoCoverIcon(
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp,
    backgroundSize: Dp = 64.dp,
) {
    PlaylistCardIcon(
        modifier = modifier
            .size(backgroundSize),
        brush = Brush.linearGradient(
            colors = listOf(
                ButtonPrimary,
                ButtonPrimary.copy(alpha = 0.2f)
            ),
        ),
        alpha = 0.14f,
        icon = {
            Icon(
                modifier = Modifier.size(iconSize)
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
                imageVector =  ImageVector.vectorResource(R.drawable.playlist_gradient),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    )
}

@Composable
fun CardCreateIcon(
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp,
    backgroundSize: Dp = 64.dp,
) {
    PlaylistCardIcon(
        modifier = modifier
            .size(backgroundSize),
        brush = Brush.linearGradient(
            colors = listOf(
                TextSecondary,
                TextSecondary.copy(alpha = 0.2f)
            ),
        ),
        alpha = 0.14f,
        icon = {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector =  ImageVector.vectorResource(R.drawable.plus),
                contentDescription = null,
                tint = TextSecondary
            )
        }
    )
}
