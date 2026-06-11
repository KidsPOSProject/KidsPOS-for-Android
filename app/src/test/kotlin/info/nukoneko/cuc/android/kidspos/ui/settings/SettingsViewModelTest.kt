package info.nukoneko.cuc.android.kidspos.ui.settings

import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository = fakeSettingsRepository()

    @Test
    fun initialStateReflectsStoredSettings() = runTest {
        settingsRepository.setServerAddress("http://10.0.0.2:8080")
        settingsRepository.setRunningMode(Mode.PRODUCTION)

        val viewModel = SettingsViewModel(settingsRepository)

        assertEquals("http://10.0.0.2:8080", viewModel.uiState.value.serverAddress)
        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)
    }

    @Test
    fun serverAddressChangeIsPersistedAndReflected() = runTest {
        val viewModel = SettingsViewModel(settingsRepository)

        viewModel.onServerAddressChange("http://192.168.1.10:8080")

        assertEquals("http://192.168.1.10:8080", viewModel.uiState.value.serverAddress)
    }

    @Test
    fun toggleModeSwitchesBetweenPracticeAndProduction() = runTest {
        val viewModel = SettingsViewModel(settingsRepository)
        assertEquals(Mode.PRACTICE, viewModel.uiState.value.mode)

        viewModel.onToggleMode()
        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)

        viewModel.onToggleMode()
        assertEquals(Mode.PRACTICE, viewModel.uiState.value.mode)
    }
}
