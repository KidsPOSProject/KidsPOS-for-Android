package info.nukoneko.cuc.android.kidspos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.util.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverAddress: String = "",
    val mode: Mode = Mode.PRACTICE
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.serverAddress.collect { address ->
                _uiState.update { it.copy(serverAddress = address) }
            }
        }
        viewModelScope.launch {
            settingsRepository.runningMode.collect { mode ->
                _uiState.update { it.copy(mode = mode) }
            }
        }
    }

    fun onServerAddressChange(value: String) {
        viewModelScope.launch { settingsRepository.setServerAddress(value) }
    }

    fun onToggleMode() {
        val next = _uiState.value.mode.toggle()
        viewModelScope.launch { settingsRepository.setRunningMode(next) }
    }
}
