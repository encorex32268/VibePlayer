package com.lihan.vibeplayer.ui.design_system.surface

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lihan.vibeplayer.ui.theme.SurfaceBG

@Composable
fun VPSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = SurfaceBG,
        modifier = modifier,
    ) {
        content()
    }
}
