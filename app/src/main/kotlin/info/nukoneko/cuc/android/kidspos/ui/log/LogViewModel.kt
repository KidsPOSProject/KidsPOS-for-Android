package info.nukoneko.cuc.android.kidspos.ui.log

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import info.nukoneko.cuc.android.kidspos.log.LogEntry
import info.nukoneko.cuc.android.kidspos.log.LogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class LogUiState(val entries: List<LogEntry> = emptyList())

@HiltViewModel
class LogViewModel @Inject constructor(
    private val logRepository: LogRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LogUiState(logRepository.entries.value))
    val uiState: StateFlow<LogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            logRepository.entries.collect { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }

    fun onClear() {
        viewModelScope.launch { logRepository.clear() }
    }

    fun shareText(): String {
        return _uiState.value.entries.joinToString(separator = "\n\n") { entry ->
            "${entry.headerText(includeTag = true)}\n${entry.message}"
        }
    }
}

internal fun LogEntry.formattedTime(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(timestamp))

internal fun LogEntry.levelLabel(): String = when (priority) {
    Log.WARN -> "WARN"
    Log.ERROR -> "ERROR"
    Log.ASSERT -> "ASSERT"
    else -> priority.toString()
}

internal fun LogEntry.headerText(includeTag: Boolean = false): String {
    val tagSuffix = if (includeTag) tag?.let { " $it" } ?: "" else ""
    return "${formattedTime()} ${levelLabel()}$tagSuffix"
}
