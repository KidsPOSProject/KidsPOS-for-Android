package info.nukoneko.cuc.android.kidspos.update

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApkInstallResultBusTest {

    @Test
    fun resultEmittedBeforeCollectIsReplayedToLateCollector() = runTest {
        val bus = ApkInstallResultBus()

        bus.emit(ApkInstallResult.FAILURE)

        assertEquals(ApkInstallResult.FAILURE, bus.results.first())
    }

    @Test
    fun latestResultOverwritesPreviousOne() = runTest {
        val bus = ApkInstallResultBus()

        bus.emit(ApkInstallResult.FAILURE)
        bus.emit(ApkInstallResult.SUCCESS)

        assertEquals(ApkInstallResult.SUCCESS, bus.results.first())
    }

    @Test
    fun clearRemovesReplayedResult() = runTest {
        val bus = ApkInstallResultBus()

        bus.emit(ApkInstallResult.SUCCESS)
        bus.clear()

        assertTrue(bus.results.replayCache.isEmpty())
    }
}
