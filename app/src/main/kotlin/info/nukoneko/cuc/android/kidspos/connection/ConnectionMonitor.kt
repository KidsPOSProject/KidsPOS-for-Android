package info.nukoneko.cuc.android.kidspos.connection

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.ApiHttpException
import info.nukoneko.cuc.android.kidspos.data.repository.ServerStatusRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionMonitor @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reachabilityProbe: ReachabilityProbe,
    private val serverStatusRepository: ServerStatusRepository,
    private val applicationScope: CoroutineScope
) {
    private val _state = MutableStateFlow(ConnectionCheck())
    val state: StateFlow<ConnectionCheck> = _state.asStateFlow()

    private var runningCheck: Deferred<ConnectionCheck>? = null
    private var lastCheckedAddress: String? = null

    init {
        applicationScope.launch {
            settingsRepository.serverAddress.collect { address ->
                val previous = lastCheckedAddress
                if (previous != null && address != previous) {
                    _state.value = ConnectionCheck()
                }
            }
        }
    }

    suspend fun check(): ConnectionCheck {
        val deferred = synchronized(this) {
            runningCheck?.takeIf { it.isActive }
                ?: applicationScope.async { runCheck() }.also { runningCheck = it }
        }
        val result = deferred.await()
        synchronized(this) {
            if (runningCheck === deferred) runningCheck = null
        }
        return result
    }

    fun launchCheck() {
        applicationScope.launch { check() }
    }

    private suspend fun runCheck(): ConnectionCheck {
        val address = settingsRepository.serverAddress.first()
        lastCheckedAddress = address
        val url = address.toHttpUrlOrNull()
            ?: return publish(ConnectionCheck(reachability = StageStatus.FAILED, failure = ConnectionFailure.InvalidAddress))

        _state.value = ConnectionCheck(reachability = StageStatus.CHECKING)
        try {
            withTimeout(STAGE_TIMEOUT_MILLIS) { reachabilityProbe.probe(url.host, url.port) }
        } catch (e: TimeoutCancellationException) {
            return publish(ConnectionCheck(reachability = StageStatus.FAILED, failure = ConnectionFailure.Timeout), e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val failure = ConnectionFailure.Unreachable(
                listOfNotNull(e::class.simpleName, e.message).joinToString(": ")
            )
            return publish(ConnectionCheck(reachability = StageStatus.FAILED, failure = failure), e)
        }

        _state.value = ConnectionCheck(reachability = StageStatus.OK, response = StageStatus.CHECKING)
        val serverStatus = try {
            withTimeout(STAGE_TIMEOUT_MILLIS) { serverStatusRepository.getServerStatus() }
        } catch (e: TimeoutCancellationException) {
            return publish(
                ConnectionCheck(reachability = StageStatus.OK, response = StageStatus.FAILED, failure = ConnectionFailure.Timeout),
                e
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiHttpException) {
            val failure = ConnectionFailure.HttpStatus(e.code)
            return publish(ConnectionCheck(reachability = StageStatus.OK, response = StageStatus.FAILED, failure = failure), e)
        } catch (e: Exception) {
            val failure = ConnectionFailure.Other(
                listOfNotNull(e::class.simpleName, e.message).joinToString(": ")
            )
            return publish(ConnectionCheck(reachability = StageStatus.OK, response = StageStatus.FAILED, failure = failure), e)
        }

        if (serverStatus.apiVersion != APIService.SUPPORTED_API_VERSION) {
            val failure = ConnectionFailure.ApiVersionMismatch(serverStatus.apiVersion)
            return publish(
                ConnectionCheck(StageStatus.OK, StageStatus.FAILED, failure, serverStatus)
            )
        }

        return publish(ConnectionCheck(StageStatus.OK, StageStatus.OK, null, serverStatus))
    }

    private fun publish(result: ConnectionCheck, cause: Throwable? = null): ConnectionCheck {
        _state.value = result
        if (result.failure != null) {
            Timber.w(cause, "Connection check failed: %s", result.failure)
        }
        return result
    }

    private companion object {
        const val STAGE_TIMEOUT_MILLIS = 3000L
    }
}
