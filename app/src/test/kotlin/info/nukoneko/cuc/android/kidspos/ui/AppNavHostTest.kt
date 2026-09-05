package info.nukoneko.cuc.android.kidspos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class AppNavHostTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var navController: NavHostController

    private fun setContent() {
        composeRule.setContent {
            navController = rememberNavController()
            AppNavHost(
                navController = navController,
                startupScreen = { onNavigateToMain, onNavigateToSettings ->
                    Column {
                        Text(
                            text = "to-main",
                            modifier = Modifier
                                .testTag(STARTUP_MAIN_TAG)
                                .clickable { onNavigateToMain() }
                        )
                        Text(
                            text = "to-settings",
                            modifier = Modifier
                                .testTag(STARTUP_SETTINGS_TAG)
                                .clickable { onNavigateToSettings() }
                        )
                    }
                },
                mainScreen = { onNavigateToSettings ->
                    Text(
                        text = "main",
                        modifier = Modifier
                            .testTag(MAIN_TAG)
                            .clickable { onNavigateToSettings() }
                    )
                },
                settingsScreen = { onNavigateBack, onNavigateToLogs ->
                    Column {
                        Text(
                            text = "settings",
                            modifier = Modifier
                                .testTag(SETTINGS_TAG)
                                .clickable { onNavigateBack() }
                        )
                        Text(
                            text = "logs",
                            modifier = Modifier
                                .testTag(TO_LOGS_TAG)
                                .clickable { onNavigateToLogs() }
                        )
                    }
                },
                logScreen = { onNavigateBack ->
                    Text(
                        text = "logs",
                        modifier = Modifier
                            .testTag(LOGS_TAG)
                            .clickable { onNavigateBack() }
                    )
                }
            )
        }
        composeRule.waitForIdle()
    }

    private fun currentRoute(): String? =
        composeRule.runOnIdle { navController.currentBackStackEntry?.destination?.route }

    @Test
    fun startupScreenIsShownFirst() {
        setContent()

        composeRule.onNodeWithTag(STARTUP_MAIN_TAG).assertIsDisplayed()
        assertEquals(StartupRoute, currentRoute())
    }

    @Test
    fun startupToMainRemovesStartupFromBackStack() {
        setContent()

        composeRule.onNodeWithTag(STARTUP_MAIN_TAG).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(MAIN_TAG).assertIsDisplayed()
        assertEquals(MainRoute, currentRoute())
        assertNull(navController.previousBackStackEntry)
    }

    @Test
    fun startupToSettingsThenBackGoesToMain() {
        setContent()

        composeRule.onNodeWithTag(STARTUP_SETTINGS_TAG).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(SETTINGS_TAG).assertIsDisplayed()
        assertEquals(SettingsRoute, currentRoute())

        composeRule.onNodeWithTag(SETTINGS_TAG).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(MAIN_TAG).assertIsDisplayed()
        assertEquals(MainRoute, currentRoute())
        assertNull(navController.previousBackStackEntry)
    }

    @Test
    fun navigatesToSettingsAndBackToMain() {
        setContent()

        composeRule.onNodeWithTag(STARTUP_MAIN_TAG).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(MAIN_TAG).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(SETTINGS_TAG).assertIsDisplayed()
        assertEquals(SettingsRoute, currentRoute())

        composeRule.onNodeWithTag(SETTINGS_TAG).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(MAIN_TAG).assertIsDisplayed()
        assertEquals(MainRoute, currentRoute())
    }

    @Test
    fun settingsToLogsAndBack() {
        setContent()

        composeRule.onNodeWithTag(STARTUP_MAIN_TAG).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(MAIN_TAG).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(SETTINGS_TAG).assertIsDisplayed()

        composeRule.onNodeWithTag(TO_LOGS_TAG).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(LOGS_TAG).assertIsDisplayed()
        assertEquals(LogsRoute, currentRoute())

        composeRule.onNodeWithTag(LOGS_TAG).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(SETTINGS_TAG).assertIsDisplayed()
        assertEquals(SettingsRoute, currentRoute())
    }

    @Test
    fun repeatedBackTapsDuringTransitionKeepMainScreenVisible() {
        setContent()

        composeRule.onNodeWithTag(STARTUP_MAIN_TAG).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(MAIN_TAG).performClick()
        composeRule.waitForIdle()

        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithTag(SETTINGS_TAG).performClick()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithTag(SETTINGS_TAG).performClick()
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(MAIN_TAG).assertIsDisplayed()
        assertEquals(MainRoute, currentRoute())
    }

    @Test
    fun repeatedSettingsTapsDoNotStackSettingsScreen() {
        setContent()

        composeRule.onNodeWithTag(STARTUP_MAIN_TAG).performClick()
        composeRule.waitForIdle()

        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithTag(MAIN_TAG).performClick()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithTag(MAIN_TAG).performClick()
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(SETTINGS_TAG).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(MAIN_TAG).assertIsDisplayed()
        assertEquals(MainRoute, currentRoute())
    }

    private companion object {
        const val STARTUP_MAIN_TAG = "startup-to-main"
        const val STARTUP_SETTINGS_TAG = "startup-to-settings"
        const val MAIN_TAG = "main-screen"
        const val SETTINGS_TAG = "settings-screen"
        const val TO_LOGS_TAG = "settings-to-logs"
        const val LOGS_TAG = "logs-screen"
    }
}
