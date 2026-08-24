package info.nukoneko.cuc.android.kidspos.update

import info.nukoneko.cuc.android.kidspos.data.repository.AppUpdateRepository
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

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

/**
 * アプリ更新の進行状態をアプリ全体で保持する
 *
 * 画面の ViewModel に状態を持たせると、別の画面へ移動した時点で
 * viewModelScope ごとダウンロードが打ち切られ進捗も失われるため、
 * Singleton とアプリケーションスコープのコルーチンで保持する。
 */
@Singleton
class AppUpdateManager @Inject constructor(
    private val appUpdateRepository: AppUpdateRepository,
    private val apkInstaller: ApkInstaller,
    private val apkInstallResultBus: ApkInstallResultBus,
    private val applicationScope: CoroutineScope
) {
    private val _status = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val status: StateFlow<UpdateStatus> = _status.asStateFlow()

    private var runningJob: Job? = null

    init {
        applicationScope.launch {
            apkInstallResultBus.results.collect { result ->
                _status.value = when (result) {
                    ApkInstallResult.SUCCESS -> UpdateStatus.UpToDate
                    ApkInstallResult.CANCELLED -> UpdateStatus.Idle
                    ApkInstallResult.FAILURE -> UpdateStatus.Failed(UpdateFailure.INSTALL)
                }
                apkInstallResultBus.clear()
            }
        }
    }

    fun checkForUpdate(currentVersionCode: Int) {
        if (isBusy()) return
        _status.value = UpdateStatus.Checking
        runningJob = applicationScope.launch {
            val status = try {
                val update = appUpdateRepository.checkForUpdate(currentVersionCode)
                if (update == null) UpdateStatus.UpToDate else UpdateStatus.Available(update)
            } catch (e: Exception) {
                Timber.w(e, "Failed to check app update")
                UpdateStatus.Failed(UpdateFailure.CHECK)
            }
            _status.value = status
        }
    }

    fun startUpdate() {
        if (isBusy()) return
        val update = (_status.value as? UpdateStatus.Available)?.update ?: return
        if (!apkInstaller.canRequestInstall()) {
            _status.value = UpdateStatus.InstallNotPermitted
            return
        }
        _status.value = UpdateStatus.Downloading(0f)
        runningJob = applicationScope.launch {
            try {
                val apk = appUpdateRepository.downloadApk(update) { progress ->
                    _status.update { current ->
                        if (current is UpdateStatus.Downloading) {
                            UpdateStatus.Downloading(progress)
                        } else {
                            current
                        }
                    }
                }
                _status.value = UpdateStatus.Installing
                apkInstaller.install(apk)
            } catch (e: Exception) {
                Timber.w(e, "Failed to update app")
                val cause = if (_status.value is UpdateStatus.Installing) {
                    UpdateFailure.INSTALL
                } else {
                    UpdateFailure.DOWNLOAD
                }
                _status.value = UpdateStatus.Failed(cause)
            }
        }
    }

    fun dismiss() {
        if (isBusy()) return
        _status.value = UpdateStatus.Idle
    }

    // インストールは apkInstaller.install() から戻った後もブロードキャスト待ちが残るため、
    // ジョブの生存だけでなく状態も見て進行中かどうかを判定する
    private fun isBusy(): Boolean =
        runningJob?.isActive == true ||
            _status.value is UpdateStatus.Checking ||
            _status.value is UpdateStatus.Downloading ||
            _status.value is UpdateStatus.Installing
}
