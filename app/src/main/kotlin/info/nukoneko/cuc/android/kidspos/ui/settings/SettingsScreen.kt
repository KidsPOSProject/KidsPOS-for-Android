package info.nukoneko.cuc.android.kidspos.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.URLUtil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import info.nukoneko.cuc.android.kidspos.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val contents = result.contents
        if (contents != null && URLUtil.isValidUrl(contents)) {
            viewModel.onServerAddressChange(contents)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.drawer_setting)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.current_connetion_target))
            OutlinedTextField(
                value = state.serverAddress,
                onValueChange = viewModel::onServerAddressChange,
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(stringResource(R.string.current_mode_format, state.mode.modeName))
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                stringResource(
                    R.string.current_version_format,
                    state.currentVersionName,
                    state.currentVersionCode
                )
            )
            Spacer(modifier = Modifier.padding(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(16.dp))
            DangerZoneSection(
                state = state,
                onPasswordChange = viewModel::onDangerZonePasswordChange,
                onUnlock = viewModel::onUnlockDangerZone,
                onLock = viewModel::onLockDangerZone
            )
            if (state.dangerZoneUnlocked) {
                Spacer(modifier = Modifier.padding(16.dp))
                Button(
                    onClick = { scanLauncher.launch(ScanOptions()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.load_setting))
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = viewModel::onToggleMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.switch_mode_format, state.mode.toggle().modeName))
                }
                Spacer(modifier = Modifier.padding(16.dp))
                AppUpdateSection(
                    state = state,
                    onCheckUpdate = viewModel::onCheckUpdate
                )
            }
        }
    }

    val status = state.updateStatus
    if (status is UpdateStatus.Available) {
        UpdateConfirmDialog(
            status = status,
            onConfirm = viewModel::onStartUpdate,
            onDismiss = viewModel::onDismissUpdate
        )
    }
}

@Composable
private fun DangerZoneSection(
    state: SettingsUiState,
    onPasswordChange: (String) -> Unit,
    onUnlock: () -> Unit,
    onLock: () -> Unit
) {
    Text(stringResource(R.string.danger_zone))
    Spacer(modifier = Modifier.padding(4.dp))
    Text(stringResource(R.string.danger_zone_description))

    when (val status = state.dangerZoneStatus) {
        is DangerZoneStatus.Checking -> {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(stringResource(R.string.danger_zone_checking))
        }

        is DangerZoneStatus.Locked -> {
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                value = state.dangerZonePassword,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(R.string.danger_zone_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                onClick = onUnlock,
                enabled = state.dangerZonePassword.isNotEmpty() && !state.dangerZoneVerifying,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.danger_zone_unlock))
            }
            if (state.dangerZoneVerifying) {
                Spacer(modifier = Modifier.padding(4.dp))
                Text(stringResource(R.string.danger_zone_unlocking))
            }
            when (val error = status.error) {
                null -> Unit
                is DangerZoneError.Rejected -> {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(error.message)
                }

                is DangerZoneError.Unreachable -> {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(stringResource(R.string.danger_zone_verify_failed))
                }
            }
        }

        is DangerZoneStatus.Unlocked -> {
            val message = when (status.reason) {
                DangerZoneReason.NOT_CONFIGURED -> R.string.danger_zone_not_configured
                DangerZoneReason.STATUS_UNAVAILABLE -> R.string.danger_zone_status_failed
                DangerZoneReason.VERIFIED -> R.string.danger_zone_unlocked
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Text(stringResource(message))
            if (status.reason == DangerZoneReason.VERIFIED) {
                Spacer(modifier = Modifier.padding(4.dp))
                TextButton(onClick = onLock) {
                    Text(stringResource(R.string.danger_zone_lock))
                }
            }
        }
    }
}

@Composable
private fun AppUpdateSection(
    state: SettingsUiState,
    onCheckUpdate: () -> Unit
) {
    val context = LocalContext.current
    val status = state.updateStatus

    Text(stringResource(R.string.app_update))
    Spacer(modifier = Modifier.padding(8.dp))
    Button(
        onClick = onCheckUpdate,
        enabled = status !is UpdateStatus.Checking &&
            status !is UpdateStatus.Downloading &&
            status !is UpdateStatus.Installing,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.check_update))
    }

    when (status) {
        is UpdateStatus.Idle, is UpdateStatus.Available -> Unit

        is UpdateStatus.Checking -> {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(stringResource(R.string.checking_update))
        }

        is UpdateStatus.UpToDate -> {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(stringResource(R.string.app_is_up_to_date))
        }

        is UpdateStatus.Downloading -> {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                stringResource(
                    R.string.downloading_update_format,
                    (status.progress * 100).roundToInt()
                )
            )
            Spacer(modifier = Modifier.padding(4.dp))
            LinearProgressIndicator(
                progress = { status.progress },
                modifier = Modifier.fillMaxWidth()
            )
        }

        is UpdateStatus.Installing -> {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(stringResource(R.string.installing_update))
        }

        is UpdateStatus.InstallNotPermitted -> {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(stringResource(R.string.install_permission_required))
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.open_install_permission_setting))
            }
        }

        is UpdateStatus.Failed -> {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                when (status.cause) {
                    UpdateFailure.CHECK -> stringResource(R.string.update_check_failed)
                    UpdateFailure.DOWNLOAD -> stringResource(R.string.update_download_failed)
                    UpdateFailure.INSTALL -> stringResource(R.string.update_install_failed)
                }
            )
        }
    }
}

@Composable
private fun UpdateConfirmDialog(
    status: UpdateStatus.Available,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.update_available)) },
        text = {
            Column {
                Text(
                    stringResource(
                        R.string.update_version_format,
                        status.update.versionName,
                        status.update.versionCode
                    )
                )
                val releaseNotes = status.update.releaseNotes
                if (!releaseNotes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(releaseNotes)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.update_now))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.update_later))
            }
        }
    )
}
