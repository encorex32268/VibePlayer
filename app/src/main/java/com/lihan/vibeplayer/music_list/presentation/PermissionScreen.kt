@file:OptIn(ExperimentalPermissionsApi::class)

package com.lihan.vibeplayer.music_list.presentation

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.ui.design_system.buttons.VPButton
import com.lihan.vibeplayer.ui.design_system.surface.VPSurface
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun PermissionScreenRoot(
    audioPermissionState: PermissionState,
    onNavigateToSetting: () -> Unit,
    modifier: Modifier = Modifier,
){
    
    PermissionScreen(
        audioPermissionState = audioPermissionState,
        onNavigateToSetting = onNavigateToSetting,
        modifier = modifier
    )
}

@Composable
fun PermissionScreen(
    audioPermissionState: PermissionState,
    onNavigateToSetting: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shouldShowRationale by remember {
        mutableStateOf(false)
    }
    var isShowRationaleDialog by remember {
        mutableStateOf(false)
    }
    var isPermanentlyDenied by remember {
        mutableStateOf(false)
    }
    var showSettingsDialog by remember {
        mutableStateOf(false)
    }
    var hasRequestedPermission by remember {
        mutableStateOf(false)
    }

    when (val status = audioPermissionState.status) {
        is PermissionStatus.Denied -> {
            shouldShowRationale = status.shouldShowRationale
            if (hasRequestedPermission && !status.shouldShowRationale) {
                isPermanentlyDenied = true
            }
        }
        else -> Unit
    }
    if (showSettingsDialog){
        AlertDialog(
            containerColor = SurfaceBG,
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.permission_denied_title)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.permission_denied_message)
                )
            },
            confirmButton = {
                VPButton(
                    text = stringResource(R.string.permission_denied_settings),
                    onClick = {
                        showSettingsDialog = false
                        onNavigateToSetting()
                    }
                )
            },
            dismissButton = {
                VPButton(
                    text = stringResource(R.string.permission_dialog_cancel),
                    onClick = {
                        showSettingsDialog = false
                    }
                )
            }
        )
    }

    // 顯示 Rationale 對話框
    if (isShowRationaleDialog){
        AlertDialog(
            containerColor = SurfaceBG,
            onDismissRequest = { isShowRationaleDialog = false},
            title = {
                Text(
                    text = stringResource(R.string.permission_rationale_title)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.permission_rationale_message)
                )
            },
            confirmButton = {
                VPButton(
                    text = stringResource(R.string.permission_dialog_try_again),
                    onClick = {
                        isShowRationaleDialog = false
                        audioPermissionState.launchPermissionRequest()
                    }
                )
            },
            dismissButton = {
                VPButton(
                    text = stringResource(R.string.permission_dialog_ok),
                    onClick = {
                        isShowRationaleDialog = false
                    }
                )
            }
        )
    }
    
    VPSurface{
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.logo),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.permission_description),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            VPButton(
                text = stringResource(R.string.permission_allow_button),
                onClick = {
                    if (isPermanentlyDenied && !shouldShowRationale){
                        // 權限被永久拒絕，顯示引導到設定頁的對話框
                        showSettingsDialog = true
                    } else if (shouldShowRationale){
                        // 第一次被拒絕且有 Rationale，顯示說明對話框
                        isShowRationaleDialog = true
                    } else {
                        // 第一次請求權限
                        hasRequestedPermission = true
                        audioPermissionState.launchPermissionRequest()
                    }
                }
            )
        }

    }

}

@Preview
@Composable
private fun PermissionScreenPreview() {
    VibePlayerTheme {
        PermissionScreen(
            audioPermissionState = rememberPermissionState(
                permission = Manifest.permission.READ_MEDIA_AUDIO
            ),
            onNavigateToSetting = {}
        )
    }
}