package com.lihan.vibeplayer.ui.design_system.buttons

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.theme.ButtonPrimary
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun VPFloatingActionButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        content = content,
        containerColor = ButtonPrimary,
        contentColor = TextPrimary,
        shape = CircleShape
    )

}

@Preview
@Composable
private fun VPFloatingActionButtonPreview() {
    VibePlayerTheme {
        VPFloatingActionButton(
            onClick = {},
            content = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_up),
                    contentDescription = null
                )
            }
        )
    }
}