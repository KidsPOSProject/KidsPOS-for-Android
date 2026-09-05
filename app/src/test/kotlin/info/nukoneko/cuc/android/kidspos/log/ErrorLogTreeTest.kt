package info.nukoneko.cuc.android.kidspos.log

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import timber.log.Timber
import java.io.File

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ErrorLogTreeTest {
    private lateinit var logRepository: LogRepository

    @Before
    fun setUp() {
        val dispatcher = Dispatchers.Unconfined
        logRepository = LogRepository(
            File.createTempFile("error_log", ".json"),
            Json,
            dispatcher,
            CoroutineScope(dispatcher)
        )
        Timber.plant(ErrorLogTree(logRepository))
    }

    @After
    fun tearDown() {
        Timber.uprootAll()
    }

    @Test
    fun debugAndInfoAreNotRecorded() {
        Timber.d("debug")
        Timber.i("info")

        assertTrue(logRepository.entries.value.isEmpty())
    }

    @Test
    fun warnAndErrorAreRecorded() {
        Timber.w("w")
        Timber.e("e")

        val entries = logRepository.entries.value
        assertEquals(2, entries.size)
        assertEquals(Log.ERROR, entries[0].priority)
        assertEquals(Log.WARN, entries[1].priority)
    }

    @Test
    fun throwableStackTraceIsIncludedInMessage() {
        Timber.e(IllegalStateException("boom"), "failed")

        val message = logRepository.entries.value[0].message
        assertTrue(message.contains("failed"))
        assertTrue(message.contains("IllegalStateException"))
        assertTrue(message.contains("boom"))
    }
}
