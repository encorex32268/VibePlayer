package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.design_system.buttons.VPButton
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun EmptyView(
    onScanAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.main_not_found_title),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.main_not_found_description),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        VPButton(
            text = stringResource(R.string.main_not_found_scan_again),
            onClick = onScanAgainClick
        )
    }

}

@Preview
@Composable
private fun EmptyViewPreview() {
    VibePlayerTheme {
        EmptyView(
            onScanAgainClick = {}
        )
    }
}