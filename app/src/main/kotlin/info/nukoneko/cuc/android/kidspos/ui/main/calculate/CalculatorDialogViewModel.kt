package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.RequestStatus
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CalculatorDialogViewModel(
        private val api: APIService,
        private val config: GlobalConfig,
        private val event: EventBus) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val progressViewVisibility = MutableLiveData<Int>()
    fun getProgressViewVisibility(): LiveData<Int> = progressViewVisibility

    private val totalPriceText = MutableLiveData<String>()
    fun getTotalPriceText(): LiveData<String> = totalPriceText

    private val totalPrice: Int
        get() = totalPriceText.value?.toIntOrNull() ?: 0

    private var deposit: Int = 0
        set(value) {
            field = value
            depositText.postValue("$value")
        }

    private val depositText = MutableLiveData<String>()
    @Suppress("unused")
    fun getDepositText(): LiveData<String> = depositText

    private lateinit var items: List<Item>
    private var requestStatus: RequestStatus = RequestStatus.IDLE
        set(value) {
            field = value
            when (value) {
                RequestStatus.IDLE -> {
                    progressViewVisibility.value = View.GONE
                }
                RequestStatus.REQUESTING -> {
                    progressViewVisibility.value = View.VISIBLE
                }
                RequestStatus.SUCCESS -> {
                    progressViewVisibility.value = View.GONE
                }
                RequestStatus.FAILURE -> {
                    progressViewVisibility.value = View.GONE
                }
            }
        }

    init {
        totalPriceText.value = "0"
        deposit = 200
    }

    fun setup(items: List<Item>, totalPrice: Int) {
        this.items = items
        this.totalPriceText.value = "$totalPrice"
    }

    var listener: Listener? = null

    fun onAccount() {
        if (config.isPracticeModeEnabled) {
            listener?.onShouldShowErrorMessage("練習モードのためレシートは出ません")
            listener?.onDismiss()
            return
        }

        if (requestStatus == RequestStatus.REQUESTING) {
            return
        }

        requestStatus = RequestStatus.REQUESTING
        launch {
            try {
                val sale: Sale? = requestCreateSale()
                event.post(SystemEvent.SentSaleSuccess.also {
                    it.value = sale
                })
                requestStatus = RequestStatus.SUCCESS
            } catch (e: Throwable) {
                requestStatus = RequestStatus.FAILURE
            }
            listener?.onDismiss()
        }
    }

    fun onDoneClick(@Suppress("UNUSED_PARAMETER") view: View) {
        listener?.onShouldShowResultDialog(totalPrice, deposit)
    }

    fun onCancelClick(@Suppress("UNUSED_PARAMETER") view: View) {
        listener?.onDismiss()
    }

    private suspend fun requestCreateSale() = withContext(Dispatchers.IO) {
        api.createSale(deposit,
                items.size,
                totalPrice,
                items.map { it.id }.joinToString(","),
                config.currentStore?.id ?: 0,
                config.currentStaff?.barcode ?: "").await()
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
            Math.floor((deposit / 10).toDouble()).toInt()
        }
    }

    interface Listener {
        fun onShouldShowResultDialog(totalPrice: Int, deposit: Int)

        fun onShouldShowErrorMessage(message: String)

        fun onDismiss()
    }
}