package com.lihan.vibeplayer.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.theme.ButtonHover28
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDisabledStyle: Boolean = false,
    isRemoveBackground: Boolean = false
) {
    val containerColor = if (isDisabledStyle) Color.Transparent  else ButtonHover28
    val contentColor = if (isDisabledStyle) TextDisabled else TextSecondary
    val iconTintColor = if (isDisabledStyle) TextDisabled else TextSecondary
    IconButton(
        modifier = modifier.clip(CircleShape),
        onClick = onClick,
        colors = IconButtonColors(
            containerColor = if (isRemoveBackground) Color.Transparent else containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextDisabled
        )
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = icon,
            contentDescription = "scan",
            tint = iconTintColor
        )
    }

}

@Preview
@Composable
private fun CircleIconButtonPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.scan),
                onClick = {}
            )
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.scan),
                onClick = {},
                isRemoveBackground = true
            )
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.scan),
                onClick = {},
                isDisabledStyle = true
            )
        }
    }
}