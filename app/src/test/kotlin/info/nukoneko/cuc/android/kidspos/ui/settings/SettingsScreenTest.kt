package info.nukoneko.cuc.android.kidspos.ui.settings

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.testutil.FakePreferencesDataStore
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class SettingsScreenTest {
    private val mainDispatcherRule = MainDispatcherRule()
    private val composeRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(mainDispatcherRule).around(composeRule)

    private val settingsRepository = SettingsRepository(FakePreferencesDataStore(), Json)

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun currentServerAddressIsShownInTextField() {
        val viewModel = SettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(viewModel.uiState.value.serverAddress).assertIsDisplayed()
    }

    @Test
    fun typingServerAddressUpdatesState() {
        val viewModel = SettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNode(hasSetTextAction()).performTextReplacement("http://10.0.0.5:8080")
        composeRule.waitForIdle()

        assertEquals("http://10.0.0.5:8080", viewModel.uiState.value.serverAddress)
    }

    @Test
    fun toggleModeButtonSwitchesToProduction() {
        val viewModel = SettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(
            context.getString(R.string.switch_mode_format, Mode.PRODUCTION.modeName)
        ).performClick()
        composeRule.waitForIdle()

        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)
    }

    @Test
    fun backButtonInvokesNavigateBack() {
        var navigatedBack = false
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = { navigatedBack = true },
                viewModel = SettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).performClick()

        assertTrue(navigatedBack)
    }
}
