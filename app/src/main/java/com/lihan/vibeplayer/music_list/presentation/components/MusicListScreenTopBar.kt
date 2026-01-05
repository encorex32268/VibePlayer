package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.ui.theme.Accent
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun MusicListScreenTopBar(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.logo),
                contentDescription = "Logo",
                tint = Color.Unspecified
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Accent
            )
        }
        CircleIconButton(
            icon = ImageVector.vectorResource(R.drawable.scan),
            onClick = onScanClick
        )
    }

}

@Preview
@Composable
private fun MusicListScreenTopBarPreview() {
    VibePlayerTheme {
        MusicListScreenTopBar(
            onScanClick = {}
        )
    }
}