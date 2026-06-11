package info.nukoneko.cuc.android.kidspos.ui.main

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.data.repository.ItemRepository
import info.nukoneko.cuc.android.kidspos.data.repository.SaleRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StaffRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StoreRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.FakePreferencesDataStore
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeInput
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import kotlinx.serialization.json.Json
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
    private val settingsRepository = SettingsRepository(FakePreferencesDataStore(), Json)
    private val barcodeEventBus = BarcodeEventBus()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun createViewModel(): MainViewModel {
        val dispatcher = mainDispatcherRule.dispatcher
        return MainViewModel(
            ItemRepository(apiService, dispatcher),
            StaffRepository(apiService, dispatcher),
            StoreRepository(apiService, dispatcher),
            SaleRepository(apiService, dispatcher),
            settingsRepository,
            barcodeEventBus
        )
    }

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
}
