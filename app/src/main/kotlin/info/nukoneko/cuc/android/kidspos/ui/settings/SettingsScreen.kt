package info.nukoneko.cuc.android.kidspos.ui.settings

import android.webkit.URLUtil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.util.Mode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val contents = result.contents
        if (contents != null && URLUtil.isValidUrl(contents)) {
            viewModel.onServerAddressChange(contents)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.drawer_setting)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = context.getString(R.string.back)
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
                .padding(16.dp)
        ) {
            Text(context.getString(R.string.current_connetion_target))
            OutlinedTextField(
                value = state.serverAddress,
                onValueChange = viewModel::onServerAddressChange,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                onClick = { scanLauncher.launch(ScanOptions()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(context.getString(R.string.load_setting))
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Text(context.getString(R.string.current_mode_format, state.mode.modeName))
            Spacer(modifier = Modifier.padding(8.dp))
            val nextMode = when (state.mode) {
                Mode.PRODUCTION -> Mode.PRACTICE
                Mode.PRACTICE -> Mode.PRODUCTION
            }
            Button(
                onClick = viewModel::onToggleMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(context.getString(R.string.switch_mode_format, nextMode.modeName))
            }
        }
    }
}
