package info.nukoneko.cuc.android.kidspos.ui.main

import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.data.repository.ItemRepository
import info.nukoneko.cuc.android.kidspos.data.repository.SaleRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StaffRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StoreRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.FakePreferencesDataStore
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeInput
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val apiService = FakeAPIService()
    private val settingsRepository = SettingsRepository(FakePreferencesDataStore(), Json)
    private val barcodeEventBus = BarcodeEventBus()

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

    private fun emitBarcode(barcode: String, kind: BarcodeKind) {
        barcodeEventBus.emit(BarcodeInput(barcode, kind))
    }

    private fun MainViewModel.enterDeposit(vararg digits: Int) {
        digits.forEach { onCalculatorNumber(it) }
    }

    @Test
    fun itemBarcodeAddsItemAndUpdatesTotal() = runTest {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "item", price = 300, storeId = 1, genreId = 1)
        }
        val viewModel = createViewModel()

        emitBarcode("1001000001", BarcodeKind.ITEM)
        emitBarcode("1001000002", BarcodeKind.ITEM)

        assertEquals(2, viewModel.uiState.value.items.size)
        assertEquals(600, viewModel.uiState.value.total)
    }

    @Test
    fun itemFetchFailureShowsItemError() = runTest {
        apiService.getItemHandler = { throw RuntimeException("network") }
        val viewModel = createViewModel()

        emitBarcode("1001000001", BarcodeKind.ITEM)

        assertEquals(R.string.request_item_failed, viewModel.uiState.value.errorMessageRes)
        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun staffBarcodeStoresStaff() = runTest {
        val viewModel = createViewModel()

        emitBarcode("1000000001", BarcodeKind.STAFF)

        assertEquals(Staff("1000000001", "staff"), viewModel.uiState.value.staff)
    }

    @Test
    fun staffFetchFailureShowsStaffError() = runTest {
        apiService.getStaffHandler = { throw RuntimeException("network") }
        val viewModel = createViewModel()

        emitBarcode("1000000001", BarcodeKind.STAFF)

        assertEquals(R.string.request_staff_failed, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun saleBarcodeShowsReceiptError() = runTest {
        val viewModel = createViewModel()

        emitBarcode("1002000001", BarcodeKind.SALE)

        assertEquals(R.string.read_receipt_failed, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun unknownBarcodeIsIgnored() = runTest {
        val viewModel = createViewModel()

        emitBarcode("1099000001", BarcodeKind.UNKNOWN)

        assertEquals(MainUiState(), viewModel.uiState.value)
    }

    @Test
    fun accountFlowComputesChange() = runTest {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "item", price = 300, storeId = 1, genreId = 1)
        }
        val viewModel = createViewModel()
        emitBarcode("1001000001", BarcodeKind.ITEM)

        viewModel.onAccountClick()
        val calculator = viewModel.uiState.value.calculator
        assertNotNull(calculator)
        assertEquals(300, calculator?.totalPrice)
        assertEquals(false, calculator?.accountEnabled)

        viewModel.enterDeposit(5, 0, 0)
        assertEquals(500, viewModel.uiState.value.calculator?.deposit)
        assertEquals(true, viewModel.uiState.value.calculator?.accountEnabled)

        viewModel.onCalculatorOk()
        val result = viewModel.uiState.value.accountResult
        assertNull(viewModel.uiState.value.calculator)
        assertEquals(300, result?.totalPrice)
        assertEquals(500, result?.deposit)
        assertEquals(200, result?.change)
    }

    @Test
    fun calculatorClearRemovesLastDigit() = runTest {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "item", price = 300, storeId = 1, genreId = 1)
        }
        val viewModel = createViewModel()
        emitBarcode("1001000001", BarcodeKind.ITEM)
        viewModel.onAccountClick()
        viewModel.enterDeposit(5, 0, 0)

        viewModel.onCalculatorClear()
        assertEquals(50, viewModel.uiState.value.calculator?.deposit)

        viewModel.onCalculatorClear()
        assertEquals(5, viewModel.uiState.value.calculator?.deposit)

        viewModel.onCalculatorClear()
        assertEquals(0, viewModel.uiState.value.calculator?.deposit)
    }

    @Test
    fun calculatorOkIsIgnoredWhenDepositIsInsufficient() = runTest {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "item", price = 300, storeId = 1, genreId = 1)
        }
        val viewModel = createViewModel()
        emitBarcode("1001000001", BarcodeKind.ITEM)
        viewModel.onAccountClick()
        viewModel.enterDeposit(1, 0, 0)

        viewModel.onCalculatorOk()

        assertNotNull(viewModel.uiState.value.calculator)
        assertNull(viewModel.uiState.value.accountResult)
    }

    @Test
    fun accountResultBackReturnsToCalculatorKeepingDeposit() = runTest {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "item", price = 300, storeId = 1, genreId = 1)
        }
        val viewModel = createViewModel()
        emitBarcode("1001000001", BarcodeKind.ITEM)
        viewModel.onAccountClick()
        viewModel.enterDeposit(5, 0, 0)
        viewModel.onCalculatorOk()

        viewModel.onAccountResultBack()

        assertNull(viewModel.uiState.value.accountResult)
        assertEquals(500, viewModel.uiState.value.calculator?.deposit)
    }

    @Test
    fun practiceModeAccountSkipsApiAndClearsItems() = runTest {
        apiService.getItemHandler = { barcode ->
            Item(id = 1, barcode = barcode, name = "item", price = 300, storeId = 1, genreId = 1)
        }
        val viewModel = createViewModel()
        emitBarcode("1001000001", BarcodeKind.ITEM)
        viewModel.onAccountClick()
        viewModel.enterDeposit(5, 0, 0)
        viewModel.onCalculatorOk()

        viewModel.onAccountResultOk()

        assertEquals(R.string.practice_mode_no_receipt, viewModel.uiState.value.toastMessageRes)
        assertTrue(viewModel.uiState.value.items.isEmpty())
        assertEquals(0, viewModel.uiState.value.total)
        assertTrue(apiService.createSaleCalls.isEmpty())

        viewModel.onToastShown()
        assertNull(viewModel.uiState.value.toastMessageRes)
    }

    @Test
    fun productionModeAccountSendsSale() = runTest {
        apiService.getItemHandler = { barcode ->
            Item(id = 9, barcode = barcode, name = "item", price = 300, storeId = 1, genreId = 1)
        }
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        settingsRepository.setCurrentStore(Store(3, "ストア"))
        val viewModel = createViewModel()
        emitBarcode("1000000001", BarcodeKind.STAFF)
        emitBarcode("1001000001", BarcodeKind.ITEM)
        viewModel.onAccountClick()
        viewModel.enterDeposit(5, 0, 0)
        viewModel.onCalculatorOk()

        viewModel.onAccountResultOk()

        val args = apiService.createSaleCalls.single()
        assertEquals(3, args.storeId)
        assertEquals("1000000001", args.staffBarcode)
        assertEquals(500, args.deposit)
        assertEquals("9", args.itemIds)
        assertTrue(viewModel.uiState.value.items.isEmpty())
        assertNull(viewModel.uiState.value.accountResult)
    }

    @Test
    fun productionModeAccountFailureShowsServerMessage() = runTest {
        apiService.createSaleHandler = { throw RuntimeException("boom") }
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val viewModel = createViewModel()
        emitBarcode("1001000001", BarcodeKind.ITEM)
        viewModel.onAccountClick()
        viewModel.enterDeposit(5, 0, 0)
        viewModel.onCalculatorOk()

        viewModel.onAccountResultOk()

        assertEquals("boom", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun productionModeAccountFailureWithoutMessageShowsGenericError() = runTest {
        apiService.createSaleHandler = { throw RuntimeException() }
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val viewModel = createViewModel()
        emitBarcode("1001000001", BarcodeKind.ITEM)
        viewModel.onAccountClick()
        viewModel.enterDeposit(5, 0, 0)
        viewModel.onCalculatorOk()

        viewModel.onAccountResultOk()

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(R.string.network_error, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun changeStoreInPracticeModeShowsDummyStores() = runTest {
        val viewModel = createViewModel()

        viewModel.onChangeStoreClick()

        val selection = viewModel.uiState.value.storeSelection
        assertNotNull(selection)
        assertEquals(false, selection?.loading)
        assertEquals(2, selection?.stores?.size)
        assertTrue(apiService.createSaleCalls.isEmpty())
    }

    @Test
    fun changeStoreInProductionModeFetchesStores() = runTest {
        val stores = listOf(Store(1, "ストアA"), Store(2, "ストアB"), Store(3, "ストアC"))
        apiService.fetchStoresHandler = { stores }
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val viewModel = createViewModel()

        viewModel.onChangeStoreClick()

        assertEquals(stores, viewModel.uiState.value.storeSelection?.stores)
    }

    @Test
    fun changeStoreFailureMarksSelectionAsFailed() = runTest {
        apiService.fetchStoresHandler = { throw RuntimeException("network") }
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val viewModel = createViewModel()

        viewModel.onChangeStoreClick()

        assertEquals(true, viewModel.uiState.value.storeSelection?.failed)
    }

    @Test
    fun selectingStorePersistsAndClosesSelection() = runTest {
        val viewModel = createViewModel()
        viewModel.onChangeStoreClick()

        viewModel.onStoreSelected(Store(2, "デパート"))

        assertNull(viewModel.uiState.value.storeSelection)
        assertEquals(Store(2, "デパート"), viewModel.uiState.value.store)
        assertEquals(Store(2, "デパート"), settingsRepository.currentStore.first())
    }

    @Test
    fun errorDismissClearsBothMessageKinds() = runTest {
        val viewModel = createViewModel()
        emitBarcode("1002000001", BarcodeKind.SALE)
        assertNotNull(viewModel.uiState.value.errorMessageRes)

        viewModel.onErrorDismiss()

        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.errorMessageRes)
    }
}
