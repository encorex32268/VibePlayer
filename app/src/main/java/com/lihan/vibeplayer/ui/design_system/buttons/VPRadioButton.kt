package com.lihan.vibeplayer.ui.design_system.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.ui.theme.ButtonPrimary
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun VPRadioButton(
    selected: Boolean,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    RadioButton(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        colors = RadioButtonColors(
            selectedColor = ButtonPrimary,
            unselectedColor = TextSecondary,
            disabledSelectedColor = TextDisabled,
            disabledUnselectedColor = TextDisabled,
        )
    )

}

@Preview
@Composable
private fun VPRadioButtonPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            VPRadioButton(
                selected = true,
                enabled = true
            )
            VPRadioButton(
                selected = false,
                enabled = true
            )
            VPRadioButton(
                selected = true,
                enabled = false
            )
            VPRadioButton(
                selected = false,
                enabled = false
            )
        }
    }
}