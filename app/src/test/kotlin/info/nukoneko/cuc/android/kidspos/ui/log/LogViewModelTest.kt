package info.nukoneko.cuc.android.kidspos.ui.log

import android.util.Log
import info.nukoneko.cuc.android.kidspos.log.LogEntry
import info.nukoneko.cuc.android.kidspos.log.LogRepository
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LogViewModelTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createRepository(fileName: String = "log.json"): LogRepository {
        val dispatcher = mainDispatcherRule.dispatcher
        return LogRepository(
            File(temporaryFolder.root, fileName),
            Json,
            dispatcher,
            CoroutineScope(dispatcher)
        )
    }

    private fun entry(message: String) = LogEntry(
        timestamp = System.currentTimeMillis(),
        priority = Log.WARN,
        tag = "Tag",
        message = message
    )

    @Test
    fun entriesReflectRepositoryNewestFirst() = runTest {
        val repository = createRepository()
        val viewModel = LogViewModel(repository)

        repository.append(entry("first"))
        repository.append(entry("second"))

        assertEquals("second", viewModel.uiState.value.entries[0].message)
        assertEquals("first", viewModel.uiState.value.entries[1].message)
    }

    @Test
    fun clearEmptiesEntries() = runTest {
        val repository = createRepository()
        val viewModel = LogViewModel(repository)
        repository.append(entry("to-clear"))

        viewModel.onClear()

        assertTrue(viewModel.uiState.value.entries.isEmpty())
    }

    @Test
    fun shareTextContainsHeaderAndMessage() = runTest {
        val repository = createRepository()
        val viewModel = LogViewModel(repository)
        repository.append(LogEntry(0L, Log.ERROR, "Tag", "boom\ntrace"))

        val shareText = viewModel.shareText()

        assertTrue(shareText.contains("ERROR"))
        assertTrue(shareText.contains("Tag"))
        assertTrue(shareText.contains("boom"))
        assertTrue(shareText.contains("trace"))
    }
}
