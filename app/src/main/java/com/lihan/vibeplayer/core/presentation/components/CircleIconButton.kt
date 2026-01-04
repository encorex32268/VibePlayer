package com.lihan.vibeplayer.core.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.clip(CircleShape),
        onClick = onClick,
        colors = IconButtonColors(
            containerColor = ButtonHover28,
            contentColor = TextSecondary,
            disabledContainerColor = ButtonHover28,
            disabledContentColor = TextDisabled
        )
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = icon,
            contentDescription = "scan"
        )
    }

}

@Preview
@Composable
private fun CircleIconButtonPreview() {
    VibePlayerTheme {
        CircleIconButton(
            icon = ImageVector.vectorResource(R.drawable.scan),
            onClick = {}
        )
    }
}