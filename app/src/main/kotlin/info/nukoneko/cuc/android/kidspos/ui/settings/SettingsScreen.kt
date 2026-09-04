package info.nukoneko.cuc.android.kidspos.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.URLUtil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.update.UpdateFailure
import info.nukoneko.cuc.android.kidspos.update.UpdateStatus
import info.nukoneko.cuc.android.kidspos.util.Mode
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            ConnectionCard(
                serverAddress = state.serverAddress,
                onLoadSetting = { scanLauncher.launch(ScanOptions()) },
                modifier = Modifier.weight(1f)
            )
            ModeCard(
                currentMode = state.mode,
                onToggleMode = viewModel::onToggleMode,
                modifier = Modifier.weight(1f)
            )
            AppUpdateCard(
                state = state,
                onCheckUpdate = viewModel::onCheckUpdate,
                modifier = Modifier.weight(1f)
            )
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
private fun SettingsCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            content()
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.heightIn(min = 56.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ConnectionCard(
    serverAddress: String,
    onLoadSetting: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsCard(
        title = stringResource(R.string.current_connetion_target),
        modifier = modifier
    ) {
        Text(text = serverAddress, style = MaterialTheme.typography.bodyLarge)
        ActionButton(text = stringResource(R.string.load_setting), onClick = onLoadSetting)
    }
}

@Composable
private fun ModeCard(
    currentMode: Mode,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsCard(
        title = stringResource(R.string.current_mode),
        modifier = modifier
    ) {
        val modes = listOf(Mode.PRACTICE, Mode.PRODUCTION)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            modes.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = currentMode == mode,
                    onClick = { if (currentMode != mode) onToggleMode() },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                    modifier = Modifier.heightIn(min = 56.dp),
                    label = { Text(mode.modeName) }
                )
            }
        }
    }
}

@Composable
private fun AppUpdateCard(
    state: SettingsUiState,
    onCheckUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsCard(
        title = stringResource(R.string.app_update),
        modifier = modifier
    ) {
        AppUpdateSection(state = state, onCheckUpdate = onCheckUpdate)
    }
}

@Composable
private fun AppUpdateSection(
    state: SettingsUiState,
    onCheckUpdate: () -> Unit
) {
    val context = LocalContext.current
    val status = state.updateStatus

    Text(
        text = stringResource(
            R.string.current_version_format,
            state.currentVersionName,
            state.currentVersionCode
        ),
        style = MaterialTheme.typography.bodyLarge
    )
    ActionButton(
        text = stringResource(R.string.check_update),
        onClick = onCheckUpdate,
        enabled = status !is UpdateStatus.Checking &&
            status !is UpdateStatus.Downloading &&
            status !is UpdateStatus.Installing
    )

    when (status) {
        is UpdateStatus.Idle, is UpdateStatus.Available -> Unit

        is UpdateStatus.Checking -> {
            Text(stringResource(R.string.checking_update))
        }

        is UpdateStatus.UpToDate -> {
            Text(stringResource(R.string.app_is_up_to_date))
        }

        is UpdateStatus.Downloading -> {
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
            Text(stringResource(R.string.installing_update))
        }

        is UpdateStatus.InstallNotPermitted -> {
            Text(stringResource(R.string.install_permission_required))
            ActionButton(
                text = stringResource(R.string.open_install_permission_setting),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    }
                }
            )
        }

        is UpdateStatus.Failed -> {
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
