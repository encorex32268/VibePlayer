package com.lihan.vibeplayer.ui.design_system.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun VPChip(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)?=null
) {
    SuggestionChip(
        modifier = modifier,
        onClick = {
            onClick?.invoke()
        },
        enabled = enabled,
        shape = RoundedCornerShape(100),
        border = BorderStroke(
            width = 1.dp,
            color = SurfaceOutline
        ),
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    )

}

@Preview
@Composable
private fun VPChipPreview() {
    VibePlayerTheme {
        VPChip(
            text = "Showing",
            onClick = {},
            enabled = false
        )
    }
}