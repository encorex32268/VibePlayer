package com.lihan.vibeplayer.music_list.presentation.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.ui.theme.TextPrimary
import kotlin.math.roundToInt


private val TabRowIndicatorSpec: AnimationSpec<Dp> =
    tween(durationMillis = 250, easing = FastOutSlowInEasing)

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    currentTabWidth: Dp
): Modifier =
    composed{
        val indicatorOffset by
        animateDpAsState(
            targetValue = currentTabPosition.left + ((currentTabPosition.width - currentTabWidth) / 2) ,
            animationSpec = TabRowIndicatorSpec
        )
        fillMaxWidth()
            .wrapContentSize(Alignment.BottomStart)
            .width(currentTabWidth)
            .offset {
                IntOffset(
                    x = indicatorOffset.roundToPx(),
                    y = 0
                )
            }
            .clip(RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))


    }