@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.ui.design_system.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.RippleDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.ui.theme.ButtonHover
import com.lihan.vibeplayer.ui.theme.ButtonPrimary
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun VPTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val rippleConfiguration = RippleConfiguration(
        color = ButtonHover,
        rippleAlpha = RippleDefaults.RippleAlpha
    )

    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {

        TextButton(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.textButtonColors(
                contentColor = ButtonPrimary,
                disabledContentColor = TextDisabled,
            )
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }


}


@Preview
@Composable
private fun VPTextButtonPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            VPTextButton(
                text = "Button",
                onClick = {}
            )
            VPTextButton(
                text = "Button",
                onClick = {},
                enabled = false
            )
        }
    }
}