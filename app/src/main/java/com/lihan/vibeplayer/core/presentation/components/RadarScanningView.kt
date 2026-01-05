package com.lihan.vibeplayer.core.presentation.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.ui.theme.Accent
import com.lihan.vibeplayer.ui.theme.ScannerArcGradient
import com.lihan.vibeplayer.ui.theme.ScannerLineGradient
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarScanningView(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val biggerStroke = with(density) { 1.5.dp.toPx() }
    val smallerStroke = biggerStroke / 2


    val outerCircleRadius = with(density) {64.dp.toPx()}
    val circleRadius = with(density) { 48.dp.toPx()}
    val innerCircleRadius =  with(density) { 32.dp.toPx() }
    val moreInnerCircleRadius =  with(density) { 16.dp.toPx() }
    val centerCircleRadius =  with(density) { 4.dp.toPx() }

    val infiniteTransition = rememberInfiniteTransition()
    val animatable by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            ),
        )
    )

    Canvas(modifier) {

        drawCircle(
            color = Accent.copy(alpha = 0.1f),
            style = Stroke(
                width = smallerStroke
            ),
            radius = outerCircleRadius
        )
        drawCircle(
            color = Accent,
            style = Stroke(
                width = biggerStroke
            ),
            radius = circleRadius
        )
        drawCircle(
            color = Accent.copy(alpha = 0.1f),
            style = Stroke(
                width = smallerStroke
            ),
            radius = innerCircleRadius
        )
        drawCircle(
            color = Accent.copy(alpha = 0.1f),
            style = Stroke(
                width = smallerStroke
            ),
            radius = moreInnerCircleRadius
        )
        drawCircle(
            color = Accent,
            radius = centerCircleRadius
        )

        rotate(degrees = animatable , pivot = center){
            drawArc(
                brush = ScannerArcGradient,
                startAngle = 0f,
                sweepAngle = 135f,
                useCenter = true,
                size = Size(circleRadius * 2, circleRadius * 2),
                topLeft = Offset(center.x - circleRadius, center.y - circleRadius)
            )
            val angleInRadians = 135f * (Math.PI.toFloat() / 180f)
            val dx = circleRadius * cos(angleInRadians)
            val dy = circleRadius * sin(angleInRadians)

            drawLine(
                brush = ScannerLineGradient,
                start = center,
                end = center + Offset(dx, dy),
                strokeWidth = smallerStroke,
                cap = StrokeCap.Round
            )
        }



    }
}

@Preview
@Composable
private fun RadarScanningViewPreview() {
    VibePlayerTheme {
        RadarScanningView(
            modifier = Modifier.size(300.dp)
        )
    }
}