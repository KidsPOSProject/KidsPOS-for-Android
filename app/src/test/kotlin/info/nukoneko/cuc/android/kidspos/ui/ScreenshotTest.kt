package info.nukoneko.cuc.android.kidspos.ui

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureScreenRoboImage
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkInstaller
import info.nukoneko.cuc.android.kidspos.testutil.FakeAppUpdateService
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createMainViewModel
import info.nukoneko.cuc.android.kidspos.testutil.createSettingsViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeInput
import info.nukoneko.cuc.android.kidspos.ui.main.MainScreen
import info.nukoneko.cuc.android.kidspos.ui.main.MainViewModel
import info.nukoneko.cuc.android.kidspos.ui.settings.SettingsScreen
import info.nukoneko.cuc.android.kidspos.ui.settings.SettingsViewModel
import info.nukoneko.cuc.android.kidspos.ui.theme.KidsPosTheme
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.Mode
import java.io.IOException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.MediumTablet)
class ScreenshotTest {
    private val mainDispatcherRule = MainDispatcherRule()
    private val composeRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(mainDispatcherRule).around(composeRule)

    private val apiService = FakeAPIService()
    private val settingsRepository = fakeSettingsRepository()
    private val barcodeEventBus = BarcodeEventBus()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun mainViewModel(): MainViewModel = createMainViewModel(
        apiService,
        settingsRepository,
        barcodeEventBus,
        mainDispatcherRule.dispatcher
    )

    private fun setUpStoreAndStaff() {
        runBlocking {
            settingsRepository.setCurrentStore(Store(1, "100リバー"))
            settingsRepository.setCurrentStaff(Staff("100", "たろう"))
        }
    }

    private fun setMainContent(viewModel: MainViewModel) {
        composeRule.setContent {
            KidsPosTheme {
                MainScreen(onNavigateToSettings = {}, viewModel = viewModel)
            }
        }
        composeRule.waitForIdle()
    }

    private fun addItems(viewModel: MainViewModel) {
        apiService.getItemHandler = { barcode ->
            when (barcode) {
                "1000000001" -> Item(1, barcode, "りんごジュース", 150, 1, 1)
                else -> Item(2, barcode, "チョコレート", 200, 1, 1)
            }
        }
        setMainContent(viewModel)
        barcodeEventBus.emit(BarcodeInput("1000000001", BarcodeKind.ITEM))
        barcodeEventBus.emit(BarcodeInput("1000000002", BarcodeKind.ITEM))
        composeRule.waitForIdle()
    }

    @Test
    fun mainScreenEmpty() {
        setUpStoreAndStaff()
        setMainContent(mainViewModel())
        captureScreenRoboImage("screenshots/main_screen_empty.png")
    }

    @Test
    fun mainScreenWithItems() {
        setUpStoreAndStaff()
        addItems(mainViewModel())
        captureScreenRoboImage("screenshots/main_screen.png")
    }

    @Test
    fun mainScreenDrawer() {
        setUpStoreAndStaff()
        setMainContent(mainViewModel())
        composeRule.onNodeWithContentDescription(
            context.getString(R.string.navigation_drawer_open)
        ).performClick()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_drawer.png")
    }

    @Test
    fun mainScreenCalculator() {
        setUpStoreAndStaff()
        val viewModel = mainViewModel()
        addItems(viewModel)
        viewModel.onAccountClick()
        viewModel.onCalculatorNumber(5)
        viewModel.onCalculatorNumber(0)
        viewModel.onCalculatorNumber(0)
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_calculator.png")
    }

    @Test
    fun mainScreenAccountResult() {
        setUpStoreAndStaff()
        val viewModel = mainViewModel()
        addItems(viewModel)
        viewModel.onAccountClick()
        viewModel.onCalculatorNumber(5)
        viewModel.onCalculatorNumber(0)
        viewModel.onCalculatorNumber(0)
        viewModel.onCalculatorOk()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_account_result.png")
    }

    @Test
    fun mainScreenStoreSelection() {
        setUpStoreAndStaff()
        val viewModel = mainViewModel()
        setMainContent(viewModel)
        viewModel.onChangeStoreClick()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_store_selection.png")
    }

    @Test
    fun mainScreenStoreSelectionLoading() {
        setUpStoreAndStaff()
        runBlocking { settingsRepository.setRunningMode(Mode.PRODUCTION) }
        apiService.fetchStoresHandler = { awaitCancellation() }
        val viewModel = mainViewModel()
        setMainContent(viewModel)
        viewModel.onChangeStoreClick()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_store_selection_loading.png")
    }

    @Test
    fun mainScreenStoreSelectionFailed() {
        setUpStoreAndStaff()
        runBlocking { settingsRepository.setRunningMode(Mode.PRODUCTION) }
        apiService.fetchStoresHandler = { throw IOException() }
        val viewModel = mainViewModel()
        setMainContent(viewModel)
        viewModel.onChangeStoreClick()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_store_selection_failed.png")
    }

    @Test
    fun mainScreenItemSelection() {
        setUpStoreAndStaff()
        apiService.fetchItemsHandler = {
            listOf(
                Item(1, "1000000001", "りんごジュース", 150, 1, 1),
                Item(2, "1000000002", "チョコレート", 200, 1, 1),
                Item(3, "1000000003", "やきそば", 300, 1, 1),
                Item(4, "1000000004", "わたあめ", 100, 1, 1),
                Item(5, "1000000005", "フランクフルト", 250, 1, 1),
                Item(6, "1000000006", "かき氷", 180, 1, 1)
            )
        }
        val viewModel = mainViewModel()
        setMainContent(viewModel)
        viewModel.onManualItemSelectionClick()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_item_selection.png")
    }

    @Test
    fun mainScreenItemSelectionLoading() {
        setUpStoreAndStaff()
        apiService.fetchItemsHandler = { awaitCancellation() }
        val viewModel = mainViewModel()
        setMainContent(viewModel)
        viewModel.onManualItemSelectionClick()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_item_selection_loading.png")
    }

    @Test
    fun mainScreenItemSelectionFailed() {
        setUpStoreAndStaff()
        apiService.fetchItemsHandler = { throw IOException() }
        val viewModel = mainViewModel()
        setMainContent(viewModel)
        viewModel.onManualItemSelectionClick()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_item_selection_failed.png")
    }

    @Test
    fun mainScreenError() {
        setUpStoreAndStaff()
        val viewModel = mainViewModel()
        setMainContent(viewModel)
        barcodeEventBus.emit(BarcodeInput("2000000001", BarcodeKind.SALE))
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/main_screen_error.png")
    }

    private fun setSettingsContent(viewModel: SettingsViewModel) {
        composeRule.setContent {
            KidsPosTheme {
                SettingsScreen(onNavigateBack = {}, viewModel = viewModel)
            }
        }
        composeRule.waitForIdle()
    }

    private fun availableUpdateService() = FakeAppUpdateService().apply {
        checkForUpdateHandler = {
            AppUpdate(
                versionName = "9.9.9",
                versionCode = 99,
                fileSize = 1024,
                releaseNotes = "レシート印刷の不具合を修正しました",
                downloadPath = "/api/apk/download/1"
            )
        }
    }

    @Test
    fun settingsScreen() {
        runBlocking { settingsRepository.setServerAddress("http://192.168.1.10:8080") }
        setSettingsContent(createSettingsViewModel(settingsRepository))
        captureScreenRoboImage("screenshots/settings_screen.png")
    }

    @Test
    fun settingsScreenUpdateAvailable() {
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = availableUpdateService()
        )
        setSettingsContent(viewModel)
        viewModel.onCheckUpdate()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/settings_screen_update_available.png")
    }

    @Test
    fun settingsScreenUpToDate() {
        val viewModel = createSettingsViewModel(settingsRepository)
        setSettingsContent(viewModel)
        viewModel.onCheckUpdate()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/settings_screen_up_to_date.png")
    }

    @Test
    fun settingsScreenUpdateCheckFailed() {
        val updateService = FakeAppUpdateService().apply {
            checkForUpdateHandler = { throw IOException() }
        }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = updateService
        )
        setSettingsContent(viewModel)
        viewModel.onCheckUpdate()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/settings_screen_update_check_failed.png")
    }

    @Test
    fun settingsScreenInstallNotPermitted() {
        val installer = FakeApkInstaller().apply { installAllowed = false }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = availableUpdateService(),
            apkInstaller = installer
        )
        setSettingsContent(viewModel)
        viewModel.onCheckUpdate()
        composeRule.waitForIdle()
        viewModel.onStartUpdate()
        composeRule.waitForIdle()
        captureScreenRoboImage("screenshots/settings_screen_install_not_permitted.png")
    }
}
