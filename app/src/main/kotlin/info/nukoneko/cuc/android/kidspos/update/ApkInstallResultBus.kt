package info.nukoneko.cuc.android.kidspos.update

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ApkInstallResult {
    SUCCESS,
    CANCELLED,
    FAILURE
}

@Singleton
class ApkInstallResultBus @Inject constructor() {
    private val _results = MutableSharedFlow<ApkInstallResult>(replay = 1)
    val results: SharedFlow<ApkInstallResult> = _results.asSharedFlow()

    fun emit(result: ApkInstallResult) {
        _results.tryEmit(result)
    }

    fun clear() {
        _results.resetReplayCache()
    }
}
