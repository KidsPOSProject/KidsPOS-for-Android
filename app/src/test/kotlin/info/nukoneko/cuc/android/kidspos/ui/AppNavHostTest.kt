package info.nukoneko.cuc.android.kidspos.ui

import androidx.compose.foundation.clickable
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
                mainScreen = { onNavigateToSettings ->
                    Text(
                        text = "main",
                        modifier = Modifier
                            .testTag(MAIN_TAG)
                            .clickable { onNavigateToSettings() }
                    )
                },
                settingsScreen = { onNavigateBack ->
                    Text(
                        text = "settings",
                        modifier = Modifier
                            .testTag(SETTINGS_TAG)
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
    fun mainScreenIsShownFirst() {
        setContent()

        composeRule.onNodeWithTag(MAIN_TAG).assertIsDisplayed()
        assertEquals(MainRoute, currentRoute())
    }

    @Test
    fun navigatesToSettingsAndBackToMain() {
        setContent()

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
    fun repeatedBackTapsDuringTransitionKeepMainScreenVisible() {
        setContent()

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
        const val MAIN_TAG = "main-screen"
        const val SETTINGS_TAG = "settings-screen"
    }
}
