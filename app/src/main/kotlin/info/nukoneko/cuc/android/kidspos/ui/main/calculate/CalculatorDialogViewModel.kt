package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Item
import info.nukoneko.cuc.android.kidspos.domain.entity.StoreId
import info.nukoneko.cuc.android.kidspos.domain.repository.SaleRepository
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.floor

class CalculatorDialogViewModel(
    private val saleRepository: SaleRepository,
    private val config: GlobalConfig,
    private val event: EventBus
) : ViewModel() {

    private val totalPriceText = MutableLiveData<String>()
    fun getTotalPriceText(): LiveData<String> = totalPriceText

    private val accountButtonEnabled = MutableLiveData<Boolean>()
    fun getAccountButtonEnabled(): LiveData<Boolean> = accountButtonEnabled

    private val totalPrice: Int
        get() = totalPriceText.value?.toIntOrNull() ?: 0

    private var deposit: Int = 0
        set(value) {
            field = value
            depositText.postValue("$value")
            accountButtonEnabled.postValue(totalPrice in 1..value)
        }

    private val depositText = MutableLiveData<String>()

    @Suppress("unused")
    fun getDepositText(): LiveData<String> = depositText

    private lateinit var items: List<Item>

    init {
        totalPriceText.value = "0"
        accountButtonEnabled.value = false
        deposit = 0
    }

    fun setup(items: List<Item>, totalPrice: Int) {
        this.items = items
        this.totalPriceText.value = "$totalPrice"
    }

    var listener: Listener? = null

    fun onOk() {
        if (config.currentRunningMode == Mode.PRACTICE) {
            listener?.onShouldShowErrorMessage("練習モードのためレシートは出ません")
            event.post(SystemEvent.SentSaleSuccess(null))
            listener?.onDismiss()
            return
        }

        val errorHandler = CoroutineExceptionHandler { _, error ->
            listener?.onShouldShowErrorMessage(error.localizedMessage)
        }

        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            val sale = saleRepository.postSale(
                storeId = config.currentStore?.id ?: StoreId(0),
                staffBarcode = config.currentStaff?.barcode ?: Barcode(""),
                deposit = deposit,
                itemList = items.map { it.id }
            )
            event.post(SystemEvent.SentSaleSuccess(sale))
            listener?.onDismiss()
        }
    }

    fun onDoneClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onShouldShowResultDialog(totalPrice, deposit)
    }

    fun onCancelClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onDismiss()
    }

    override fun onCleared() {
        listener = null
        super.onCleared()
    }

    fun onNumberClick(number: Int) {
        if (deposit > 100000) return

        deposit = if (deposit == 0) {
            number
        } else {
            deposit * 10 + number
        }
    }

    fun onClearClick() {
        deposit = if (10 > deposit) {
            0
        } else {
            floor((deposit / 10).toDouble()).toInt()
        }
    }

    interface Listener {
        fun onShouldShowResultDialog(totalPrice: Int, deposit: Int)

        fun onShouldShowErrorMessage(message: String)

        fun onDismiss()
    }
}
