package info.nukoneko.cuc.android.kidspos.data.settings

import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.testutil.FakePreferencesDataStore
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsRepositoryTest {
    private val repository = SettingsRepository(FakePreferencesDataStore(), Json)

    @Test
    fun defaultsAreReturnedWhenNothingIsStored() = runTest {
        assertEquals(SettingsRepository.DEFAULT_SERVER_ADDRESS, repository.serverAddress.first())
        assertEquals(Mode.PRACTICE, repository.runningMode.first())
        assertNull(repository.currentStore.first())
        assertNull(repository.currentStaff.first())
    }

    @Test
    fun serverAddressRoundTrips() = runTest {
        repository.setServerAddress("http://10.0.0.1:8080")
        assertEquals("http://10.0.0.1:8080", repository.serverAddress.first())
    }

    @Test
    fun runningModeRoundTrips() = runTest {
        repository.setRunningMode(Mode.PRODUCTION)
        assertEquals(Mode.PRODUCTION, repository.runningMode.first())
    }

    @Test
    fun currentStoreRoundTripsAndClears() = runTest {
        val store = Store(5, "ストア", "printer://example")
        repository.setCurrentStore(store)
        assertEquals(store, repository.currentStore.first())

        repository.setCurrentStore(null)
        assertNull(repository.currentStore.first())
    }

    @Test
    fun currentStaffRoundTripsAndClears() = runTest {
        val staff = Staff("1000000001", "たろう")
        repository.setCurrentStaff(staff)
        assertEquals(staff, repository.currentStaff.first())

        repository.setCurrentStaff(null)
        assertNull(repository.currentStaff.first())
    }
}
