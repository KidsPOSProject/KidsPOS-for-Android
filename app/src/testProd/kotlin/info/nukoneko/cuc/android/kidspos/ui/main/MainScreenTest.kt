package info.nukoneko.cuc.android.kidspos.ui.main

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createMainViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeInput
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.Mode
import java.io.IOException
import kotlin.math.abs
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する。
// デフォルト端末は 320x470dp と狭く、電卓の数字キーが画面外に置かれてタップが届かないため、
// 実際の利用端末であるタブレットの画面サイズで実行する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.MediumTablet)
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
    fun calculatorKeysAreSquareAndSameSize() {
        openCalculator()

        val labels = listOf("1", "5", "9", "0", context.getString(R.string.delete))
        val sizes = labels.map { label ->
            val bounds = composeRule.onNodeWithText(label).getUnclippedBoundsInRoot()
            label to (bounds.width.value to bounds.height.value)
        }

        sizes.forEach { (label, size) ->
            val (width, height) = size
            assertTrue("$label のキーが正方形ではない: ${width}x$height", abs(width - height) <= 1f)
            assertTrue("$label のキーが小さすぎる: ${width}x$height", width >= 48f)
        }
        val widths = sizes.map { it.second.first }
        assertTrue("キーの大きさが揃っていない: $widths", widths.max() - widths.min() <= 1f)
    }

    @Test
    fun calculatorKeysAreLaidOutInThreeColumns() {
        openCalculator()

        val left = { label: String -> composeRule.onNodeWithText(label).getUnclippedBoundsInRoot().left.value }
        val top = { label: String -> composeRule.onNodeWithText(label).getUnclippedBoundsInRoot().top.value }

        listOf("1", "4", "7").forEach { assertTrue("$it が1列目にない", abs(left(it) - left("1")) <= 1f) }
        listOf("2", "5", "8", "0").forEach { assertTrue("$it が2列目にない", abs(left(it) - left("2")) <= 1f) }
        listOf("3", "6", "9", context.getString(R.string.delete)).forEach {
            assertTrue("$it が3列目にない", abs(left(it) - left("3")) <= 1f)
        }
        assertTrue("0 が最下段にない", top("0") > top("7"))
        assertTrue(
            "けす が0と同じ段にない",
            abs(top(context.getString(R.string.delete)) - top("0")) <= 1f
        )
    }

    @Test
    fun calculatorShowsShortageUntilDepositCoversTotal() {
        openCalculator()

        composeRule.onNodeWithTag(CalculatorAccountButtonTag).assertIsNotEnabled()

        pressKeys("1", "0", "0")

        composeRule.onNodeWithText(context.getString(R.string.shortage)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.river_format, 100)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.river_format, 200)).assertIsDisplayed()
        composeRule.onNodeWithTag(CalculatorAccountButtonTag).assertIsNotEnabled()
    }

    @Test
    fun calculatorShowsChangeWhenDepositIsEnough() {
        openCalculator()

        pressKeys("5", "0", "0")

        composeRule.onNodeWithText(context.getString(R.string.change)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.river_format, 500)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.river_format, 200)).assertIsDisplayed()
        composeRule.onNodeWithTag(CalculatorAccountButtonTag).assertIsEnabled()
    }

    @Test
    fun calculatorAmountsAreLargerThanTheirLabels() {
        openCalculator()

        pressKeys("5", "0", "0")

        val labelHeight = heightOf(context.getString(R.string.deposit))
        val amountHeight = heightOf(context.getString(R.string.river_format, 500))
        assertTrue(
            "金額がラベルより大きく表示されていない: label=$labelHeight amount=$amountHeight",
            amountHeight > labelHeight
        )
    }

    @Test
    fun calculatorAmountsFitInsideTheDialogWidth() {
        openCalculator()

        pressKeys("9", "9", "9", "9", "9", "9")

        val summaryWidth = composeRule.onNodeWithTag(CalculatorSummaryTag)
            .getUnclippedBoundsInRoot().width.value
        listOf(999999, 999699).forEach { amount ->
            val width = boundsOf(context.getString(R.string.river_format, amount)).width.value
            assertTrue("$amount の表示が枠に収まっていない: $width / $summaryWidth", width < summaryWidth)
        }
    }

    @Test
    fun accountResultShowsChangeAsTheLargestText() {
        openCalculator()

        pressKeys("5", "0", "0")
        composeRule.onNodeWithTag(CalculatorAccountButtonTag).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(context.getString(R.string.change)).assertIsDisplayed()
        composeRule.onNodeWithTag(AccountResultConfirmButtonTag).assertIsDisplayed()

        val changeHeight = heightOf(context.getString(R.string.river_format, 200))
        val depositHeight = heightOf(context.getString(R.string.river_format, 500))
        assertTrue(
            "おつりがほかの金額より大きく表示されていない: change=$changeHeight deposit=$depositHeight",
            changeHeight > depositHeight * 1.5f
        )
    }

    @Test
    @Config(qualifiers = "w960dp-h600dp-land-xhdpi")
    fun calculatorFitsInsideNexus7Landscape() {
        assertCalculatorFitsIn(600f)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp-port-xhdpi")
    fun calculatorFitsInsideSmallPhonePortrait() {
        assertCalculatorFitsIn(640f)
    }

    @Test
    fun accountResultShowsProgressWhileSaleIsInFlight() {
        val gate = CompletableDeferred<Unit>()
        val defaultCreateSale = apiService.createSaleHandler
        apiService.createSaleHandler = { args ->
            gate.await()
            defaultCreateSale(args)
        }
        runBlocking { settingsRepository.setRunningMode(Mode.PRODUCTION) }
        openCalculator()

        pressKeys("5", "0", "0")
        composeRule.onNodeWithTag(CalculatorAccountButtonTag).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(AccountResultProgressTag).assertDoesNotExist()

        // ぐるぐるは終わらないアニメーションのため、waitForIdle が止まらなくなる
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithTag(AccountResultConfirmButtonTag).performClick()
        composeRule.mainClock.advanceTimeBy(FRAME_STEP_MILLIS)

        composeRule.onNodeWithTag(AccountResultProgressTag).assertIsDisplayed()
        composeRule.onNodeWithTag(AccountResultConfirmButtonTag).assertIsNotEnabled()

        gate.complete(Unit)
        composeRule.mainClock.advanceTimeBy(FRAME_STEP_MILLIS)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(AccountResultProgressTag).assertDoesNotExist()
        composeRule.onNodeWithTag(AccountResultConfirmButtonTag).assertDoesNotExist()
    }

    private fun assertCalculatorFitsIn(screenHeight: Float) {
        openCalculator()
        pressKeys("5", "0", "0")

        val dialog = composeRule.onNodeWithTag(CalculatorDialogTag).getUnclippedBoundsInRoot()
        assertTrue(
            "ダイアログが画面からはみ出している: ${dialog.height.value} / $screenHeight",
            dialog.height.value <= screenHeight
        )

        val footer = composeRule.onNodeWithTag(CalculatorAccountButtonTag)
            .getUnclippedBoundsInRoot()
        assertTrue(
            "かいけいボタンがダイアログの外にある: ${footer.bottom.value} / ${dialog.bottom.value}",
            footer.bottom.value <= dialog.bottom.value + 1f
        )

        composeRule.onNodeWithTag(CalculatorAccountButtonTag).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.back)).assertIsDisplayed()
        composeRule.onNodeWithText("0").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.delete)).assertIsDisplayed()
    }

    private fun pressKeys(vararg keys: String) {
        keys.forEach { key ->
            composeRule.onNodeWithText(key).assertIsDisplayed().performClick()
            composeRule.waitForIdle()
        }
    }

    private fun boundsOf(text: String) = composeRule.onNodeWithText(text).getUnclippedBoundsInRoot()

    private fun heightOf(text: String) = boundsOf(text).height.value

    private fun openCalculator() {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "テスト商品", price = 300, storeId = 1, genreId = 1)
        }
        composeRule.setContent {
            MainScreen(onNavigateToSettings = {}, viewModel = createViewModel())
        }

        barcodeEventBus.emit(BarcodeInput("1001000001", BarcodeKind.ITEM))
        composeRule.waitForIdle()
        composeRule.onNodeWithText(context.getString(R.string.account)).performClick()
        composeRule.waitForIdle()
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

    private companion object {
        const val FRAME_STEP_MILLIS = 100L
    }
}
