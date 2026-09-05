package info.nukoneko.cuc.android.kidspos.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.api.ApiHttpException
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.testutil.FakeAppUpdateService
import info.nukoneko.cuc.android.kidspos.testutil.FakeReachabilityProbe
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createSettingsViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.ui.connection.ConnectionStatusReachabilityTag
import info.nukoneko.cuc.android.kidspos.ui.connection.ConnectionStatusResponseTag
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.IOException

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
            SettingsScreen(onNavigateBack = {}, onNavigateToLogs = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(viewModel.uiState.value.serverAddress).assertIsDisplayed()
    }

    @Test
    fun serverAddressCannotBeEditedByHand() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, onNavigateToLogs = {}, viewModel = viewModel)
        }

        composeRule.onAllNodes(hasSetTextAction()).assertCountEquals(0)
    }

    @Test
    fun selectingProductionSegmentSwitchesMode() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, onNavigateToLogs = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(Mode.PRODUCTION.modeName).performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)
    }

    @Test
    fun selectingCurrentModeSegmentDoesNothing() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, onNavigateToLogs = {}, viewModel = viewModel)
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
                onNavigateToLogs = {},
                viewModel = createSettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).performClick()
        composeRule.waitForIdle()

        assertTrue(navigatedBack)
    }

    @Test
    fun checkUpdateButtonShowsUpToDateMessage() {
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = {},
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
                onNavigateToLogs = {},
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

    @Test
    fun updateCheckHttpErrorShowsStatusCode() {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { throw ApiHttpException(503) }
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = {},
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

        composeRule.onNodeWithText(context.getString(R.string.update_check_failed)).assertExists()
        composeRule.onNodeWithText(
            context.getString(R.string.update_failure_http_format, 503)
        ).assertExists()
    }

    @Test
    fun openInBrowserButtonIsShown() {
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = {},
                viewModel = createSettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.open_in_browser))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun openInBrowserButtonStartsViewIntentForServerAddress() {
        val viewModel = createSettingsViewModel(settingsRepository)
        composeRule.setContent {
            SettingsScreen(onNavigateBack = {}, onNavigateToLogs = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(context.getString(R.string.open_in_browser))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        val intent = shadowOf(context as Application).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(viewModel.uiState.value.serverAddress, intent.dataString)
    }

    @Test
    fun connectionTestShowsBothStagesOk() {
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = {},
                viewModel = createSettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.connection_test))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        val okText = context.getString(R.string.connection_status_ok)
        composeRule.onNodeWithTag(ConnectionStatusReachabilityTag)
            .onChildren()
            .filterToOne(hasText(okText))
            .assertIsDisplayed()
        composeRule.onNodeWithTag(ConnectionStatusResponseTag)
            .onChildren()
            .filterToOne(hasText(okText))
            .assertIsDisplayed()
    }

    @Test
    fun connectionTestShowsFailureWhenUnreachable() {
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> throw IOException("refused") }
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = {},
                viewModel = createSettingsViewModel(settingsRepository, reachabilityProbe = probe)
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.connection_test))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(ConnectionStatusReachabilityTag)
            .onChildren()
            .filterToOne(hasText(context.getString(R.string.connection_status_failed)))
            .assertIsDisplayed()
        composeRule.onNodeWithText("IOException: refused", substring = true).assertIsDisplayed()
    }

    @Test
    fun backIsBlockedInProductionUntilConnected() {
        runBlocking { settingsRepository.setRunningMode(Mode.PRODUCTION) }
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> throw IOException("refused") }
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = {},
                viewModel = createSettingsViewModel(settingsRepository, reachabilityProbe = probe)
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.connection_test))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).assertIsNotEnabled()
        composeRule.onNodeWithText(context.getString(R.string.connection_required_to_leave))
            .assertIsDisplayed()

        probe.probeHandler = { _, _ -> }
        composeRule.onNodeWithText(context.getString(R.string.connection_test))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).assertIsEnabled()
    }

    @Test
    fun errorLogButtonNavigatesToLogs() {
        var navigatedToLogs = false
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = { navigatedToLogs = true },
                viewModel = createSettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.error_log))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        assertTrue(navigatedToLogs)
    }

    @Test
    fun browserButtonIsInsideOtherCard() {
        composeRule.setContent {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToLogs = {},
                viewModel = createSettingsViewModel(settingsRepository)
            )
        }

        composeRule.onNodeWithText(context.getString(R.string.other_settings)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.open_in_browser))
            .performScrollTo()
            .assertIsDisplayed()
    }
}
