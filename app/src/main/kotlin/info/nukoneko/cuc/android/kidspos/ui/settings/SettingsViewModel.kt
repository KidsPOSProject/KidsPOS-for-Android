package info.nukoneko.cuc.android.kidspos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import info.nukoneko.cuc.android.kidspos.BuildConfig
import info.nukoneko.cuc.android.kidspos.api.DangerZoneRateLimitedException
import info.nukoneko.cuc.android.kidspos.data.repository.AppUpdateRepository
import info.nukoneko.cuc.android.kidspos.data.repository.DangerZoneRepository
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

enum class DangerZoneReason {
    NOT_CONFIGURED,
    STATUS_UNAVAILABLE,
    VERIFIED
}

sealed interface DangerZoneStatus {
    data object Checking : DangerZoneStatus
    data class Locked(val error: DangerZoneError? = null) : DangerZoneStatus
    data class Unlocked(val reason: DangerZoneReason) : DangerZoneStatus
}

sealed interface DangerZoneError {
    data class Rejected(val message: String) : DangerZoneError
    data class RateLimited(val retryAfterSeconds: Long?) : DangerZoneError
    data object Unreachable : DangerZoneError
}

data class SettingsUiState(
    val serverAddress: String = "",
    val mode: Mode = Mode.PRACTICE,
    val currentVersionName: String = BuildConfig.VERSION_NAME,
    val currentVersionCode: Int = BuildConfig.VERSION_CODE,
    val updateStatus: UpdateStatus = UpdateStatus.Idle,
    val dangerZoneStatus: DangerZoneStatus = DangerZoneStatus.Checking,
    val dangerZonePassword: String = "",
    val dangerZoneVerifying: Boolean = false
) {
    val dangerZoneUnlocked: Boolean get() = dangerZoneStatus is DangerZoneStatus.Unlocked
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val appUpdateRepository: AppUpdateRepository,
    private val dangerZoneRepository: DangerZoneRepository,
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
                apkInstallResultBus.clear()
            }
        }
        checkDangerZoneStatus()
    }

    private fun checkDangerZoneStatus() {
        viewModelScope.launch {
            val status = try {
                if (dangerZoneRepository.isPasswordConfigured()) {
                    DangerZoneStatus.Locked()
                } else {
                    DangerZoneStatus.Unlocked(DangerZoneReason.NOT_CONFIGURED)
                }
            } catch (e: Exception) {
                Timber.w(e, "Failed to get danger zone status")
                DangerZoneStatus.Unlocked(DangerZoneReason.STATUS_UNAVAILABLE)
            }
            _uiState.update { it.copy(dangerZoneStatus = status) }
        }
    }

    fun onDangerZonePasswordChange(value: String) {
        _uiState.update {
            it.copy(dangerZonePassword = value, dangerZoneStatus = DangerZoneStatus.Locked())
        }
    }

    fun onUnlockDangerZone() {
        val password = _uiState.value.dangerZonePassword
        if (password.isEmpty() || _uiState.value.dangerZoneVerifying) return
        _uiState.update { it.copy(dangerZoneVerifying = true) }
        viewModelScope.launch {
            val status = try {
                val result = dangerZoneRepository.verifyPassword(password)
                when {
                    result.valid -> DangerZoneStatus.Unlocked(DangerZoneReason.VERIFIED)
                    !result.configured -> DangerZoneStatus.Unlocked(DangerZoneReason.NOT_CONFIGURED)
                    else -> DangerZoneStatus.Locked(DangerZoneError.Rejected(result.message))
                }
            } catch (e: DangerZoneRateLimitedException) {
                Timber.w(e, "Danger zone verification is rate limited")
                DangerZoneStatus.Locked(DangerZoneError.RateLimited(e.retryAfterSeconds))
            } catch (e: Exception) {
                Timber.w(e, "Failed to verify danger zone password")
                DangerZoneStatus.Locked(DangerZoneError.Unreachable)
            }
            _uiState.update {
                it.copy(
                    dangerZoneStatus = status,
                    dangerZoneVerifying = false,
                    dangerZonePassword = if (status is DangerZoneStatus.Unlocked) {
                        ""
                    } else {
                        it.dangerZonePassword
                    }
                )
            }
        }
    }

    fun onLockDangerZone() {
        _uiState.update {
            it.copy(dangerZoneStatus = DangerZoneStatus.Checking, dangerZonePassword = "")
        }
        checkDangerZoneStatus()
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
