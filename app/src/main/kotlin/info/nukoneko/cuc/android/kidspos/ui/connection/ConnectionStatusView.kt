package info.nukoneko.cuc.android.kidspos.ui.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.connection.ConnectionCheck
import info.nukoneko.cuc.android.kidspos.connection.ConnectionFailure
import info.nukoneko.cuc.android.kidspos.connection.StageStatus

internal const val ConnectionStatusReachabilityTag = "connection_status_reachability"
internal const val ConnectionStatusResponseTag = "connection_status_response"

@Composable
fun ConnectionStatusView(check: ConnectionCheck, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StageRow(
            label = stringResource(R.string.connection_stage_reachability),
            status = check.reachability,
            modifier = Modifier.testTag(ConnectionStatusReachabilityTag)
        )
        StageRow(
            label = stringResource(R.string.connection_stage_response),
            status = check.response,
            modifier = Modifier.testTag(ConnectionStatusResponseTag)
        )
        val failure = check.failure
        if (failure != null) {
            Text(
                text = failureText(failure),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            val serverStatus = check.serverStatus
            if (check.isConnected && serverStatus != null) {
                Text(
                    text = stringResource(R.string.connection_connected_format, serverStatus.version),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun StageRow(label: String, status: StageStatus, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            when (status) {
                StageStatus.PENDING -> Unit
                StageStatus.CHECKING -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                StageStatus.OK -> Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                StageStatus.FAILED -> Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = statusText(status),
            style = MaterialTheme.typography.bodyLarge,
            color = statusColor(status)
        )
    }
}

@Composable
private fun statusText(status: StageStatus): String = when (status) {
    StageStatus.PENDING -> stringResource(R.string.connection_status_pending)
    StageStatus.CHECKING -> stringResource(R.string.connection_status_checking)
    StageStatus.OK -> stringResource(R.string.connection_status_ok)
    StageStatus.FAILED -> stringResource(R.string.connection_status_failed)
}

@Composable
private fun statusColor(status: StageStatus): Color = when (status) {
    StageStatus.OK -> MaterialTheme.colorScheme.secondary
    StageStatus.FAILED -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun failureText(failure: ConnectionFailure): String = when (failure) {
    ConnectionFailure.InvalidAddress -> stringResource(R.string.connection_failure_invalid_address)
    is ConnectionFailure.Unreachable ->
        stringResource(R.string.connection_failure_unreachable_format, failure.detail)
    ConnectionFailure.Timeout -> stringResource(R.string.connection_failure_timeout)
    is ConnectionFailure.HttpStatus ->
        stringResource(R.string.connection_failure_http_format, failure.code)
    is ConnectionFailure.ApiVersionMismatch ->
        stringResource(R.string.connection_failure_api_version_format, failure.apiVersion)
    is ConnectionFailure.Other ->
        stringResource(R.string.connection_failure_other_format, failure.description)
}
