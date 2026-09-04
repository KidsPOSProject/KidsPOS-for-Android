package info.nukoneko.cuc.android.kidspos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import info.nukoneko.cuc.android.kidspos.BuildConfig
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.update.AppUpdateManager
import info.nukoneko.cuc.android.kidspos.update.UpdateStatus
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverAddress: String = "",
    val mode: Mode = Mode.PRACTICE,
    val currentVersionName: String = BuildConfig.VERSION_NAME,
    val currentVersionCode: Int = BuildConfig.VERSION_CODE,
    val updateStatus: UpdateStatus = UpdateStatus.Idle
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val appUpdateManager: AppUpdateManager,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(updateStatus = appUpdateManager.status.value)
    )
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
        viewModelScope.launch {
            appUpdateManager.status.collect { status ->
                _uiState.update { it.copy(updateStatus = status) }
            }
        }
    }

    // 書き込み直後に画面を離れると viewModelScope が閉じて保存が取り消されるため、
    // 設定の永続化はアプリケーションスコープで行う
    fun onServerAddressChange(value: String) {
        applicationScope.launch { settingsRepository.setServerAddress(value) }
    }

    fun onToggleMode() {
        val next = _uiState.value.mode.toggle()
        applicationScope.launch { settingsRepository.setRunningMode(next) }
    }

    fun onCheckUpdate() {
        appUpdateManager.checkForUpdate(BuildConfig.VERSION_CODE)
    }

    fun onStartUpdate() {
        appUpdateManager.startUpdate()
    }

    fun onDismissUpdate() {
        appUpdateManager.dismiss()
    }
}
