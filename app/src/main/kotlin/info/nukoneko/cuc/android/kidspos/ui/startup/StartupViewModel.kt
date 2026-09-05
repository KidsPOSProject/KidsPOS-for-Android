package info.nukoneko.cuc.android.kidspos.ui.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import info.nukoneko.cuc.android.kidspos.connection.ConnectionCheck
import info.nukoneko.cuc.android.kidspos.connection.ConnectionMonitor
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StartupDestination { MAIN, SETTINGS }

data class StartupUiState(
    val connection: ConnectionCheck = ConnectionCheck(),
    val destination: StartupDestination? = null
)

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val connectionMonitor: ConnectionMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartupUiState())
    val uiState: StateFlow<StartupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            connectionMonitor.state.collect { check ->
                _uiState.update { it.copy(connection = check) }
            }
        }
        viewModelScope.launch {
            val mode = settingsRepository.runningMode.first()
            val destination = if (mode == Mode.PRACTICE) {
                StartupDestination.MAIN
            } else if (connectionMonitor.check().isConnected) {
                StartupDestination.MAIN
            } else {
                StartupDestination.SETTINGS
            }
            _uiState.update { it.copy(destination = destination) }
        }
    }
}
