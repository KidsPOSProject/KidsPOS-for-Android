package info.nukoneko.cuc.android.kidspos.log

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class LogRepository(
    private val file: File,
    private val json: Json,
    private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope
) {
    private val mutex = Mutex()
    private val _entries = MutableStateFlow<List<LogEntry>>(emptyList())
    val entries: StateFlow<List<LogEntry>> = _entries.asStateFlow()

    private val initialLoad: Deferred<Unit> = scope.async(dispatcher) {
        mutex.withLock {
            _entries.value = readFromFile()
        }
    }

    fun append(entry: LogEntry) {
        scope.launch(dispatcher) {
            initialLoad.await()
            mutex.withLock {
                val updated = (listOf(entry) + _entries.value).take(MAX_ENTRIES)
                _entries.value = updated
                writeToFile(updated)
            }
        }
    }

    suspend fun clear() = withContext(dispatcher) {
        initialLoad.await()
        mutex.withLock {
            _entries.value = emptyList()
            file.delete()
        }
    }

    private fun readFromFile(): List<LogEntry> {
        if (!file.exists()) return emptyList()
        return try {
            json.decodeFromString<List<LogEntry>>(file.readText())
        } catch (e: Throwable) {
            emptyList()
        }
    }

    private fun writeToFile(entries: List<LogEntry>) {
        try {
            file.parentFile?.mkdirs()
            file.writeText(json.encodeToString(entries))
        } catch (e: Throwable) {
            // ログ保存自体の失敗はアプリを落とさない
        }
    }

    companion object {
        const val MAX_ENTRIES = 200
    }
}
