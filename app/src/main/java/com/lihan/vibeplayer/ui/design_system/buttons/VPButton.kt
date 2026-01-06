package com.lihan.vibeplayer.ui.design_system.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.ui.theme.ButtonHover28
import com.lihan.vibeplayer.ui.theme.ButtonPrimary
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun VPButton(
    text: String,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)?=null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonColors(
            containerColor = ButtonPrimary,
            contentColor = TextPrimary,
            disabledContainerColor = ButtonHover28,
            disabledContentColor = TextDisabled
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
            ),
            color = LocalContentColor.current
        )
    }
}

@Preview
@Composable
private fun VPButtonPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            VPButton(
                text = "Button",
                enabled = true,
                onClick = {}
            )
            VPButton(
                text = "Button",
                enabled = false,
                onClick = {}
            )
        }
    }
}