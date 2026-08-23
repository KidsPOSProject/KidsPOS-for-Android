package info.nukoneko.cuc.android.kidspos.ui.main

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createMainViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeInput
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import java.io.IOException
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class MainScreenTest {
    private val mainDispatcherRule = MainDispatcherRule()
    private val composeRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(mainDispatcherRule).around(composeRule)

    private val apiService = FakeAPIService()
    private val settingsRepository = fakeSettingsRepository()
    private val barcodeEventBus = BarcodeEventBus()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun createViewModel(): MainViewModel = createMainViewModel(
        apiService,
        settingsRepository,
        barcodeEventBus,
        mainDispatcherRule.dispatcher
    )

    @Test
    fun accountButtonIsDisabledWhenCartIsEmpty() {
        composeRule.setContent {
            MainScreen(onNavigateToSettings = {}, viewModel = createViewModel())
        }

        composeRule.onNodeWithText(context.getString(R.string.account))
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun scannedItemAppearsInListAndEnablesAccountButton() {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "テスト商品", price = 300, storeId = 1, genreId = 1)
        }
        composeRule.setContent {
            MainScreen(onNavigateToSettings = {}, viewModel = createViewModel())
        }

        barcodeEventBus.emit(BarcodeInput("1001000001", BarcodeKind.ITEM))
        composeRule.waitForIdle()

        composeRule.onNodeWithText("テスト商品").assertIsDisplayed()
        composeRule.onAllNodesWithText(context.getString(R.string.river_format, 300))
            .assertCountEquals(2)
        composeRule.onNodeWithText(context.getString(R.string.account)).assertIsEnabled()
    }

    @Test
    fun manualItemSelectionFromDrawerAddsTappedItemToCart() {
        apiService.fetchItemsHandler = {
            listOf(Item(id = 7, barcode = "1001000007", name = "手動商品", price = 250, storeId = 1, genreId = 1))
        }
        composeRule.setContent {
            MainScreen(onNavigateToSettings = {}, viewModel = createViewModel())
        }

        composeRule.onNodeWithContentDescription(
            context.getString(R.string.navigation_drawer_open)
        ).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(context.getString(R.string.DrawerTitleManualItemSelection))
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("手動商品").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(context.getString(R.string.close)).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("手動商品").assertIsDisplayed()
        composeRule.onAllNodesWithText(context.getString(R.string.river_format, 250))
            .assertCountEquals(2)
        composeRule.onNodeWithText(context.getString(R.string.account)).assertIsEnabled()
    }

    @Test
    fun manualItemSelectionShowsFailureMessageWithReload() {
        apiService.fetchItemsHandler = { throw IOException() }
        val viewModel = createViewModel()
        composeRule.setContent {
            MainScreen(onNavigateToSettings = {}, viewModel = viewModel)
        }

        viewModel.onManualItemSelectionClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(context.getString(R.string.item_fetch_failed))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.reload)).assertIsDisplayed()

        apiService.fetchItemsHandler = {
            listOf(Item(id = 8, barcode = "1001000008", name = "復活商品", price = 120, storeId = 1, genreId = 1))
        }
        composeRule.onNodeWithText(context.getString(R.string.reload)).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("復活商品").assertIsDisplayed()
    }
}
