package info.nukoneko.cuc.android.kidspos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import info.nukoneko.cuc.android.kidspos.BuildConfig
import info.nukoneko.cuc.android.kidspos.data.repository.AppUpdateRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.update.ApkInstallResult
import info.nukoneko.cuc.android.kidspos.update.ApkInstallResultBus
import info.nukoneko.cuc.android.kidspos.update.ApkInstaller
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class UpdateFailure {
    CHECK,
    DOWNLOAD,
    INSTALL
}

sealed interface UpdateStatus {
    data object Idle : UpdateStatus
    data object Checking : UpdateStatus
    data object UpToDate : UpdateStatus
    data class Available(val update: AppUpdate) : UpdateStatus
    data class Downloading(val progress: Float) : UpdateStatus
    data object Installing : UpdateStatus
    data object InstallNotPermitted : UpdateStatus
    data class Failed(val cause: UpdateFailure) : UpdateStatus
}

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
    private val appUpdateRepository: AppUpdateRepository,
    private val apkInstaller: ApkInstaller,
    apkInstallResultBus: ApkInstallResultBus
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
        viewModelScope.launch {
            apkInstallResultBus.results.collect { result ->
                val status = when (result) {
                    ApkInstallResult.SUCCESS -> UpdateStatus.UpToDate
                    ApkInstallResult.CANCELLED -> UpdateStatus.Idle
                    ApkInstallResult.FAILURE -> UpdateStatus.Failed(UpdateFailure.INSTALL)
                }
                _uiState.update { it.copy(updateStatus = status) }
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

    fun onCheckUpdate() {
        _uiState.update { it.copy(updateStatus = UpdateStatus.Checking) }
        viewModelScope.launch {
            val status = try {
                val update = appUpdateRepository.checkForUpdate(BuildConfig.VERSION_CODE)
                if (update == null) UpdateStatus.UpToDate else UpdateStatus.Available(update)
            } catch (e: Exception) {
                Timber.w(e, "Failed to check app update")
                UpdateStatus.Failed(UpdateFailure.CHECK)
            }
            _uiState.update { it.copy(updateStatus = status) }
        }
    }

    fun onStartUpdate() {
        val update = (_uiState.value.updateStatus as? UpdateStatus.Available)?.update ?: return
        if (!apkInstaller.canRequestInstall()) {
            _uiState.update { it.copy(updateStatus = UpdateStatus.InstallNotPermitted) }
            return
        }
        _uiState.update { it.copy(updateStatus = UpdateStatus.Downloading(0f)) }
        viewModelScope.launch {
            try {
                val apk = appUpdateRepository.downloadApk(update) { progress ->
                    _uiState.update { state ->
                        if (state.updateStatus is UpdateStatus.Downloading) {
                            state.copy(updateStatus = UpdateStatus.Downloading(progress))
                        } else {
                            state
                        }
                    }
                }
                _uiState.update { it.copy(updateStatus = UpdateStatus.Installing) }
                apkInstaller.install(apk)
            } catch (e: Exception) {
                Timber.w(e, "Failed to update app")
                val cause = if (_uiState.value.updateStatus is UpdateStatus.Installing) {
                    UpdateFailure.INSTALL
                } else {
                    UpdateFailure.DOWNLOAD
                }
                _uiState.update { it.copy(updateStatus = UpdateStatus.Failed(cause)) }
            }
        }
    }

    fun onDismissUpdate() {
        _uiState.update { it.copy(updateStatus = UpdateStatus.Idle) }
    }
}
