package info.nukoneko.cuc.android.kidspos.ui.startup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.ui.connection.ConnectionStatusView

@Composable
fun StartupScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: StartupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.destination) {
        when (state.destination) {
            StartupDestination.MAIN -> onNavigateToMain()
            StartupDestination.SETTINGS -> onNavigateToSettings()
            null -> Unit
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
                Text(
                    text = stringResource(R.string.startup_connecting),
                    style = MaterialTheme.typography.titleLarge
                )
                ConnectionStatusView(check = state.connection, modifier = Modifier.width(360.dp))
            }
        }
    }
}
