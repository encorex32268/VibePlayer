@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.scan.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.core.presentation.components.RadarScanningView
import com.lihan.vibeplayer.scan.presentation.components.ScanFilterRadioGroup
import com.lihan.vibeplayer.ui.design_system.buttons.VPButton
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun ScanMusicScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            CircleIconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                icon = ImageVector.vectorResource(R.drawable.arrow_left),
                onClick = {
                    //TODO: Scan Music Back
                }
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.scan_music_title),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RadarScanningView(
                modifier = Modifier.size(140.dp)
            )
            ScanFilterRadioGroup(
                title = stringResource(R.string.scan_music_filter_ignore_duration_less_than),
                selected = "30s",
                radioSelects = listOf(
                    "30s","60s"
                )
            )
            ScanFilterRadioGroup(
                title = stringResource(R.string.scan_music_filter_ignore_size_less_than),
                selected = "100KB",
                radioSelects = listOf(
                    "100KB","500KB"
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            VPButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (true){
                    stringResource(R.string.scan_music_scan)
                }else{
                    stringResource(R.string.scan_music_scanning)
                },
                enabled = true,
                onClick = {

                }
            )

        }

    }
}

@Preview
@Composable
private fun ScanMusicScreenPreview() {
    VibePlayerTheme {
        ScanMusicScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}