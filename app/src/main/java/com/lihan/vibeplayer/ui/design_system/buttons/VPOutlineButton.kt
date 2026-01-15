package com.lihan.vibeplayer.ui.design_system.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun VPOutlineButton(
    text: String,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)?=null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary,
            disabledContentColor = TextDisabled
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SurfaceOutline
        )
    ) {
        if (leadingIcon != null){
            leadingIcon.invoke()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview
@Composable
private fun VPOutlineButtonPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            VPOutlineButton(
                text = "Button",
                enabled = true,
                onClick = {}
            )
            VPOutlineButton(
                text = "Button",
                enabled = false,
                onClick = {}
            )
        }
    }
}