package info.nukoneko.cuc.android.kidspos.ui.main

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.data.repository.ItemRepository
import info.nukoneko.cuc.android.kidspos.data.repository.SaleRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StaffRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StoreRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeInput
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.Mode
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.math.floor

data class MainUiState(
    val items: List<Item> = emptyList(),
    val total: Int = 0,
    val store: Store? = null,
    val staff: Staff? = null,
    val mode: Mode = Mode.PRACTICE,
    val demoMode: Boolean = ProjectSettings.DEMO_MODE,
    val calculator: CalculatorState? = null,
    val accountResult: AccountResultState? = null,
    val storeSelection: StoreSelectionState? = null,
    val errorMessage: String? = null,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val toastMessageRes: Int? = null
)

data class CalculatorState(
    val totalPrice: Int,
    val deposit: Int = 0
) {
    val accountEnabled: Boolean get() = totalPrice in 1..deposit
}

data class AccountResultState(
    val totalPrice: Int,
    val deposit: Int
) {
    val change: Int get() = deposit - totalPrice
}

data class StoreSelectionState(
    val loading: Boolean = false,
    val stores: List<Store> = emptyList(),
    val failed: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val staffRepository: StaffRepository,
    private val storeRepository: StoreRepository,
    private val saleRepository: SaleRepository,
    private val settingsRepository: SettingsRepository,
    barcodeEventBus: BarcodeEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.runningMode.collect { mode ->
                _uiState.update { it.copy(mode = mode) }
            }
        }
        viewModelScope.launch {
            settingsRepository.currentStore.collect { store ->
                _uiState.update { it.copy(store = store) }
            }
        }
        viewModelScope.launch {
            settingsRepository.currentStaff.collect { staff ->
                _uiState.update { it.copy(staff = staff) }
            }
        }
        viewModelScope.launch {
            barcodeEventBus.events.collect { onBarcodeInput(it) }
        }
    }

    private fun onBarcodeInput(input: BarcodeInput) {
        if (ProjectSettings.DEMO_MODE) {
            when (input.kind) {
                BarcodeKind.ITEM -> addItem(Item.create(input.barcode))
                BarcodeKind.STAFF -> setStaff(Staff.create(input.barcode))
                else -> {
                    addItem(Item.create(input.barcode))
                    setStaff(Staff.create(input.barcode))
                }
            }
            return
        }
        when (input.kind) {
            BarcodeKind.ITEM -> viewModelScope.launch {
                try {
                    addItem(itemRepository.getItemByBarcode(input.barcode))
                } catch (e: Throwable) {
                    Logger.e(e, "getItemByBarcode failed")
                    _uiState.update { it.copy(errorMessageRes = R.string.request_item_failed) }
                }
            }
            BarcodeKind.STAFF -> viewModelScope.launch {
                try {
                    setStaff(staffRepository.getStaffByBarcode(input.barcode))
                } catch (e: Throwable) {
                    Logger.e(e, "getStaffByBarcode failed")
                    _uiState.update { it.copy(errorMessageRes = R.string.request_staff_failed) }
                }
            }
            BarcodeKind.SALE -> _uiState.update { it.copy(errorMessageRes = R.string.read_receipt_failed) }
            BarcodeKind.UNKNOWN -> Unit
        }
    }

    private fun addItem(item: Item) {
        _uiState.update {
            val items = it.items + item
            it.copy(items = items, total = items.sumOf { i -> i.price })
        }
    }

    private fun setStaff(staff: Staff) {
        viewModelScope.launch { settingsRepository.setCurrentStaff(staff) }
    }

    fun onAccountClick() {
        val state = _uiState.value
        if (state.items.isEmpty()) return
        _uiState.update { it.copy(calculator = CalculatorState(totalPrice = state.total)) }
    }

    fun onCalculatorNumber(number: Int) {
        _uiState.update { state ->
            val calc = state.calculator ?: return@update state
            if (calc.deposit > 100000) return@update state
            val newDeposit = if (calc.deposit == 0) number else calc.deposit * 10 + number
            state.copy(calculator = calc.copy(deposit = newDeposit))
        }
    }

    fun onCalculatorClear() {
        _uiState.update { state ->
            val calc = state.calculator ?: return@update state
            val newDeposit = if (10 > calc.deposit) 0 else floor((calc.deposit / 10).toDouble()).toInt()
            state.copy(calculator = calc.copy(deposit = newDeposit))
        }
    }

    fun onCalculatorDismiss() {
        _uiState.update { it.copy(calculator = null) }
    }

    fun onCalculatorOk() {
        val calc = _uiState.value.calculator ?: return
        if (!calc.accountEnabled) return
        _uiState.update {
            it.copy(
                calculator = null,
                accountResult = AccountResultState(calc.totalPrice, calc.deposit)
            )
        }
    }

    fun onAccountResultBack() {
        val result = _uiState.value.accountResult ?: return
        _uiState.update {
            it.copy(
                accountResult = null,
                calculator = CalculatorState(result.totalPrice, result.deposit)
            )
        }
    }

    fun onAccountResultOk() {
        val state = _uiState.value
        val result = state.accountResult ?: return
        if (state.mode == Mode.PRACTICE) {
            _uiState.update {
                it.copy(
                    accountResult = null,
                    items = emptyList(),
                    total = 0,
                    toastMessageRes = R.string.practice_mode_no_receipt
                )
            }
            return
        }
        viewModelScope.launch {
            try {
                saleRepository.createSale(
                    storeId = state.store?.id ?: 0,
                    staffBarcode = state.staff?.barcode ?: "",
                    deposit = result.deposit,
                    items = state.items
                )
                _uiState.update {
                    it.copy(accountResult = null, items = emptyList(), total = 0)
                }
            } catch (e: Throwable) {
                Logger.e(e, "createSale failed")
                val message = e.localizedMessage
                _uiState.update {
                    if (message != null) {
                        it.copy(errorMessage = message)
                    } else {
                        it.copy(errorMessageRes = R.string.network_error)
                    }
                }
            }
        }
    }

    fun onChangeStoreClick() {
        _uiState.update { it.copy(storeSelection = StoreSelectionState(loading = true)) }
        if (_uiState.value.mode == Mode.PRACTICE) {
            _uiState.update {
                it.copy(storeSelection = StoreSelectionState(stores = DUMMY_STORES))
            }
            return
        }
        viewModelScope.launch {
            try {
                val stores = withTimeout(3000L) { storeRepository.fetchStores() }
                _uiState.update { it.copy(storeSelection = StoreSelectionState(stores = stores)) }
            } catch (e: Throwable) {
                Logger.e(e, "fetchStores failed")
                _uiState.update { it.copy(storeSelection = StoreSelectionState(failed = true)) }
            }
        }
    }

    fun onStoreSelected(store: Store) {
        viewModelScope.launch { settingsRepository.setCurrentStore(store) }
        _uiState.update { it.copy(storeSelection = null) }
    }

    fun onStoreSelectionDismiss() {
        _uiState.update { it.copy(storeSelection = null) }
    }

    fun onStoreSelectionReload() {
        onChangeStoreClick()
    }

    fun onErrorDismiss() {
        _uiState.update { it.copy(errorMessage = null, errorMessageRes = null) }
    }

    fun onToastShown() {
        _uiState.update { it.copy(toastMessageRes = null) }
    }

    fun onInsertDummyItem() {
        addItem(Item.create("1234567890"))
    }

    fun onInsertDummyStaff() {
        setStaff(Staff.create("1234567890"))
    }

    private companion object {
        val DUMMY_STORES = listOf(
            Store(1, "100リバー"),
            Store(2, "デパート")
        )
    }
}
