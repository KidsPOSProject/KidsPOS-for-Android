package info.nukoneko.cuc.android.kidspos.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.testutil.FakePreferencesDataStore
import info.nukoneko.cuc.android.kidspos.util.Mode
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsRepositoryTest {
    private val dataStore = FakePreferencesDataStore()
    private val repository = SettingsRepository(dataStore, Json)

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

    @Test
    fun brokenStoredValuesAreTreatedAsAbsent() = runTest {
        dataStore.edit { prefs ->
            prefs[SettingsRepository.KEY_STORE] = "壊れた値"
            prefs[SettingsRepository.KEY_STAFF] = """{"unexpected":1}"""
        }

        assertNull(repository.currentStore.first())
        assertNull(repository.currentStaff.first())
    }

    @Test
    fun unknownRunningModeFallsBackToPractice() = runTest {
        dataStore.edit { prefs ->
            prefs[SettingsRepository.KEY_RUNNING_MODE] = "UNKNOWN"
        }

        assertEquals(Mode.PRACTICE, repository.runningMode.first())
    }

    @Test
    fun readFailureFallsBackToDefaults() = runTest {
        val failing = SettingsRepository(FailingPreferencesDataStore(), Json)

        assertEquals(SettingsRepository.DEFAULT_SERVER_ADDRESS, failing.serverAddress.first())
        assertEquals(Mode.PRACTICE, failing.runningMode.first())
        assertNull(failing.currentStore.first())
        assertNull(failing.currentStaff.first())
    }

    private class FailingPreferencesDataStore : DataStore<Preferences> {
        override val data: Flow<Preferences> = flow { throw IOException("読み取り失敗") }

        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences
        ): Preferences = throw IOException("書き込み失敗")
    }
}
