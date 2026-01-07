@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.music_list.presentation.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.ObserveEvent
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.core.presentation.components.RadarScanningView
import com.lihan.vibeplayer.music_list.presentation.scan.components.ScanFilterRadioGroup
import com.lihan.vibeplayer.ui.design_system.buttons.VPButton
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun ScanMusicScreenRoot(
    onBack: () -> Unit,
    viewModel: ScanMusicViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    ObserveEvent(flow = viewModel.uiEvent) { event ->
        when(event){
            is ScanMusicUiEvent.OnShowResultSize -> {
                scope.launch {
                    snackbarState.showSnackbar(
                        message = context.getString(R.string.scan_music_scan_result, event.itemSize),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    ScanMusicScreen(
        state = state,
        snackbarHostState = snackbarState,
        onAction = { action ->
            when(action){
                ScanMusicAction.OnBackClick -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ScanMusicScreen(
    state: ScanMusicState,
    snackbarHostState: SnackbarHostState,
    onAction: (ScanMusicAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.width(400.dp).fillMaxWidth(),
                hostState = snackbarHostState
            )
        },
        containerColor = SurfaceBG
    ) { it ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                CircleIconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    icon = ImageVector.vectorResource(R.drawable.arrow_left),
                    onClick = {
                        onAction(ScanMusicAction.OnBackClick)
                    }
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.scan_music_title),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextPrimary
                )
            }
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadarScanningView(
                    modifier = Modifier.size(140.dp),
                    isActiveAnimation = state.isScanning
                )
                ScanFilterRadioGroup(
                    title = stringResource(R.string.scan_music_filter_ignore_duration_less_than),
                    selected = state.secondSelect,
                    radioSelects = state.secondSelectItems,
                    onSelect = {
                        onAction(ScanMusicAction.OnSecondSelect(it))
                    }
                )
                ScanFilterRadioGroup(
                    title = stringResource(R.string.scan_music_filter_ignore_size_less_than),
                    selected = state.sizeSelect,
                    radioSelects = state.sizeSelectItems,
                    onSelect = {
                        onAction(ScanMusicAction.OnSizeSelect(it))
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                VPButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (state.isScanning){
                        stringResource(R.string.scan_music_scanning)
                    }else{
                        stringResource(R.string.scan_music_scan)
                    },
                    enabled = !state.isScanning,
                    onClick = {
                        onAction(ScanMusicAction.OnScanClick)
                    },
                    leadingIcon = {
                        if (state.isScanning){
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = TextDisabled,
                                trackColor = Color.Transparent,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                )

            }

        }

    }

}

@Preview(showSystemUi = true)
@Composable
private fun ScanMusicScreenPreview() {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(Unit){
        scope.launch {
            snackbarState.showSnackbar(
                message = "Scan complete — 128 songs found."
            )
        }
    }

    VibePlayerTheme {
        ScanMusicScreen(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = snackbarState,
            state = ScanMusicState(
                isScanning = false
            ),
            onAction = {

            }
        )
    }
}