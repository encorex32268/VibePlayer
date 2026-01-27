package com.lihan.vibeplayer.ui.design_system.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.theme.ButtonPrimary
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun VPCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) ButtonPrimary else Color.Transparent,
        label = "backgroundColor"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        label = "iconAlpha"
    )

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .border(
                width = if (checked){ 0.dp }else{ 1.dp },
                color = if (checked) Color.Transparent else TextSecondary,
                shape = CircleShape
            )
    ) {
        Icon(
            modifier = Modifier
                .size(size)
                .graphicsLayer { alpha = iconAlpha }
                .padding(4.dp),
            imageVector = ImageVector.vectorResource(R.drawable.check_mark),
            tint = TextPrimary,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun VPCheckBoxPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            VPCheckbox(
                checked = true,
                onCheckedChange = {}
            )
            VPCheckbox(
                checked = false,
                onCheckedChange = {}
            )
        }
    }
}