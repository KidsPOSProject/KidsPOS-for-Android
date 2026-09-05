package info.nukoneko.cuc.android.kidspos.connection

import info.nukoneko.cuc.android.kidspos.entity.ServerStatus

enum class StageStatus { PENDING, CHECKING, OK, FAILED }

sealed interface ConnectionFailure {
    data object InvalidAddress : ConnectionFailure
    data class Unreachable(val detail: String) : ConnectionFailure
    data object Timeout : ConnectionFailure
    data class HttpStatus(val code: Int) : ConnectionFailure
    data class ApiVersionMismatch(val apiVersion: Int) : ConnectionFailure
    data class Other(val description: String) : ConnectionFailure
}

data class ConnectionCheck(
    val reachability: StageStatus = StageStatus.PENDING,
    val response: StageStatus = StageStatus.PENDING,
    val failure: ConnectionFailure? = null,
    val serverStatus: ServerStatus? = null
) {
    val isConnected: Boolean get() = response == StageStatus.OK
    val isChecking: Boolean
        get() = reachability == StageStatus.CHECKING || response == StageStatus.CHECKING
}
