package info.nukoneko.cuc.android.kidspos.ui.settings

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.testutil.FakeAppUpdateService
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createSettingsViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.util.Mode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.MediumTablet)
class SettingsScreenTest {
    private val mainDispatcherRule = MainDispatcherRule()
    private val composeRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(mainDispatcherRule).around(composeRule)

    private val settingsRepository = fakeSettingsRepository()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun currentServerAddressIsShown() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(viewModel.uiState.value.serverAddress).assertIsDisplayed()
    }

    @Test
    fun serverAddressCannotBeEditedByHand() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onAllNodes(hasSetTextAction()).assertCountEquals(0)
    }

    @Test
    fun selectingProductionSegmentSwitchesMode() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(Mode.PRODUCTION.modeName).performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)
    }

    @Test
    fun selectingCurrentModeSegmentDoesNothing() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(Mode.PRACTICE.modeName).performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(Mode.PRACTICE, viewModel.uiState.value.mode)
    }

    @Test
    fun backButtonInvokesNavigateBack() {
        var navigatedBack = false
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = { navigatedBack = true },
                viewModel = createSettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).performClick()

        assertTrue(navigatedBack)
    }

    @Test
    fun checkUpdateButtonShowsUpToDateMessage() {
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                viewModel = createSettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.check_update))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(context.getString(R.string.app_is_up_to_date)).assertExists()
    }

    @Test
    fun availableUpdateShowsConfirmDialog() {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = {
            AppUpdate(
                versionName = "9.9.9",
                versionCode = 99,
                fileSize = 1024,
                releaseNotes = null,
                downloadPath = "/api/apk/download/1"
            )
        }
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                viewModel = createSettingsViewModel(
                    settingsRepository,
                    appUpdateService = updateService
                )
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.check_update))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(context.getString(R.string.update_available))
            .assertIsDisplayed()
        composeRule.onNodeWithText(
            context.getString(R.string.update_version_format, "9.9.9", 99)
        ).assertIsDisplayed()
    }
}
