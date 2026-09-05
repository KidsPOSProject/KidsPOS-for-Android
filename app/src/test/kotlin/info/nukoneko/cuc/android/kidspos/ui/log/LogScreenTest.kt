package info.nukoneko.cuc.android.kidspos.ui.log

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.log.LogEntry
import info.nukoneko.cuc.android.kidspos.log.LogRepository
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.MediumTablet)
class LogScreenTest {
    private val mainDispatcherRule = MainDispatcherRule()
    private val temporaryFolder = TemporaryFolder()
    private val composeRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(mainDispatcherRule)
        .around(temporaryFolder)
        .around(composeRule)

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun createRepository(): LogRepository {
        val dispatcher = mainDispatcherRule.dispatcher
        return LogRepository(
            File(temporaryFolder.root, "log.json"),
            Json,
            dispatcher,
            CoroutineScope(dispatcher)
        )
    }

    @Test
    fun emptyStateIsShown() {
        val viewModel = LogViewModel(createRepository())
        composeRule.setContent {
            LogScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(context.getString(R.string.error_log_empty)).assertIsDisplayed()
    }

    @Test
    fun entriesShowFirstLineAndExpandDetails() {
        val repository = createRepository()
        repository.append(LogEntry(System.currentTimeMillis(), Log.ERROR, "Tag", "failed\njava.io.IOException: x"))
        val viewModel = LogViewModel(repository)
        composeRule.setContent {
            LogScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText("failed").assertIsDisplayed()
        composeRule.onNodeWithText("java.io.IOException: x").assertDoesNotExist()

        composeRule.onNodeWithText(context.getString(R.string.error_log_expand)).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("java.io.IOException: x").assertIsDisplayed()
    }

    @Test
    fun shareLaunchesSendChooser() {
        val repository = createRepository()
        repository.append(LogEntry(System.currentTimeMillis(), Log.WARN, "Tag", "warn message"))
        val viewModel = LogViewModel(repository)
        composeRule.setContent {
            LogScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(context.getString(R.string.error_log_share)).performClick()
        composeRule.waitForIdle()

        val intent = shadowOf(context as Application).nextStartedActivity
        assertEquals(Intent.ACTION_CHOOSER, intent.action)
        @Suppress("DEPRECATION")
        val sendIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        assertEquals(Intent.ACTION_SEND, sendIntent?.action)
    }

    @Test
    fun clearRemovesEntries() {
        val repository = createRepository()
        repository.append(LogEntry(System.currentTimeMillis(), Log.WARN, "Tag", "to be cleared"))
        val viewModel = LogViewModel(repository)
        composeRule.setContent {
            LogScreen(onNavigateBack = {}, viewModel = viewModel)
        }

        composeRule.onNodeWithText(context.getString(R.string.error_log_clear)).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(context.getString(R.string.error_log_empty)).assertIsDisplayed()
    }

    @Test
    fun backButtonCallsOnNavigateBack() {
        var navigatedBack = false
        composeRule.setContent {
            LogScreen(
                onNavigateBack = { navigatedBack = true },
                viewModel = LogViewModel(createRepository())
            )
        }

        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).performClick()
        composeRule.waitForIdle()

        assertTrue(navigatedBack)
    }
}
