package com.lihan.vibeplayer.main.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.main.presentation.components.MainScreenTopBar
import com.lihan.vibeplayer.ui.design_system.surface.VPSurface
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    VPSurface {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = SurfaceBG
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                MainScreenTopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    onScanClick = {
                        //TODO: Scan Click
                    }
                )

            }
        }

    }

}

@Preview(showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    VibePlayerTheme {
        MainScreen()
    }
}