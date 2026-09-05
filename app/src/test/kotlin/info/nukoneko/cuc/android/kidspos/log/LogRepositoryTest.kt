package info.nukoneko.cuc.android.kidspos.log

import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LogRepositoryTest {
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
        priority = 5,
        tag = "Tag",
        message = message
    )

    @Test
    fun appendPutsNewestFirst() = runTest {
        val repository = createRepository()

        repository.append(entry("first"))
        repository.append(entry("second"))

        assertEquals("second", repository.entries.value[0].message)
    }

    @Test
    fun entriesAreCappedAtMaxEntries() = runTest {
        val repository = createRepository()

        repeat(201) { index -> repository.append(entry("message-$index")) }

        val entries = repository.entries.value
        assertEquals(LogRepository.MAX_ENTRIES, entries.size)
        assertEquals("message-200", entries[0].message)
        assertFalse(entries.any { it.message == "message-0" })
    }

    @Test
    fun entriesArePersistedAndReloaded() = runTest {
        val fileName = "persisted.json"
        val repository = createRepository(fileName)
        repository.append(entry("persisted"))

        val reloaded = createRepository(fileName)

        assertEquals(repository.entries.value, reloaded.entries.value)
    }

    @Test
    fun clearRemovesAllEntries() = runTest {
        val fileName = "clear.json"
        val repository = createRepository(fileName)
        repository.append(entry("to-clear"))

        repository.clear()

        assertTrue(repository.entries.value.isEmpty())

        val reloaded = createRepository(fileName)
        assertTrue(reloaded.entries.value.isEmpty())
    }

    @Test
    fun corruptedFileIsTreatedAsEmpty() = runTest {
        val file = File(temporaryFolder.root, "corrupted.json")
        file.writeText("not json")
        val dispatcher = mainDispatcherRule.dispatcher

        val repository = LogRepository(file, Json, dispatcher, CoroutineScope(dispatcher))

        assertTrue(repository.entries.value.isEmpty())
    }
}
