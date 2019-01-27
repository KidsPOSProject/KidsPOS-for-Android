package info.nukoneko.cuc.android.kidspos.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.StringRes
import android.view.View
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.EventBusImpl
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.event.ApplicationEvent
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

class MainActivityViewModel(
        private val api: APIService,
        private val config: GlobalConfig,
        private val eventBus: EventBus) : ViewModel() {

    private val currentPrice = MutableLiveData<String>()
    fun getCurrentPriceText(): LiveData<String> = currentPrice

    private val currentStaff = MutableLiveData<String>()
    fun getCurrentStaffText(): LiveData<String> = currentStaff

    private val currentStaffVisibility = MutableLiveData<Int>()
    fun getCurrentStaffVisibility(): LiveData<Int> = currentStaffVisibility

    private val accountButtonEnabled = MutableLiveData<Boolean>()
    fun getAccountButtonEnabled(): LiveData<Boolean> = accountButtonEnabled

    var listener: Listener? = null

    var data: List<Item> = emptyList()
        private set(value) {
            field = value
            updateViews()
        }

    private fun currentTotal(): Int = data.sumBy { it.price }

    init {
        (eventBus as? EventBusImpl)?.getGlobalEventObserver()?.observeForever { event ->
            when (event) {
                BarcodeEvent.ReadStaffFailed -> listener?.onShouldShowMessage(R.string.request_staff_failed)
                BarcodeEvent.ReadItemFailed -> listener?.onShouldShowMessage(R.string.request_item_failed)
                BarcodeEvent.ReadReceiptFailed -> listener?.onShouldShowMessage(R.string.read_receipt_failed)
                BarcodeEvent.ReadItemSuccess -> listener?.onReadItemSuccess()
                SystemEvent.SentSaleSuccess -> listener?.onSendSaleSuccess()
                ApplicationEvent.AppModeChange -> listener?.onAppModeChange()
            }
        }

        updateViews()
    }

    fun onClickClear(@Suppress("UNUSED_PARAMETER") view: View?) {
        data = emptyList()
        updateViews()
    }

    fun onClickAccount(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onStartAccount()
    }

    fun onResume() {
        onStaffReadSuccess(config.currentStaff)

        requestUpdateTitle()

        if (!config.isPracticeModeEnabled) {
            GlobalScope.launch {
                if (!checkReachable(config.serverUrl, config.serverPort).await()) {
                    GlobalScope.launch(context = Dispatchers.Main) {
                        listener?.onNotReachableServer()
                    }
                }
            }
        }
    }

    private fun requestUpdateTitle() {
        var titleSuffix = ""
        config.currentStore?.name?.also { titleSuffix += " [$it]" }

        if (config.isPracticeModeEnabled) titleSuffix += " [練習モード]"

        @Suppress("ConstantConditionIf")
        if (ProjectSettings.TEST_MODE) titleSuffix += " [テストモード]"

        listener?.onChangeTitleSuffix(titleSuffix)
    }

    fun onBarcodeInput(barcode: String, prefix: BarcodeKind) {
        @Suppress("ConstantConditionIf")
        if (ProjectSettings.TEST_MODE) {
            when (prefix) {
                BarcodeKind.ITEM -> {
                    onItemReadSuccess(Item.create(barcode))
                    eventBus.post(BarcodeEvent.ReadItemSuccess)
                }
                BarcodeKind.STAFF -> {
                    onStaffReadSuccess(Staff.create(barcode))
                    eventBus.post(BarcodeEvent.ReadStaffSuccess)
                }
                else -> {
                    onItemReadSuccess(Item.create(barcode))
                    onStaffReadSuccess(Staff.create(barcode))
                    eventBus.post(BarcodeEvent.ReadItemSuccess)
                    eventBus.post(BarcodeEvent.ReadStaffSuccess)
                }
            }
        } else {
            // サーバから取得する
            when (prefix) {
                BarcodeKind.ITEM -> api.getItem(barcode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(config.getDefaultSubscribeScheduler())
                        .subscribe({ item ->
                            onItemReadSuccess(item)
                            eventBus.post(BarcodeEvent.ReadItemSuccess)
                        }, {
                            eventBus.post(BarcodeEvent.ReadItemFailed)
                        })
                BarcodeKind.STAFF -> api.getStaff(barcode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(config.getDefaultSubscribeScheduler())
                        .subscribe({ staff ->
                            onStaffReadSuccess(staff)
                            eventBus.post(BarcodeEvent.ReadStaffSuccess)
                        }, {
                            eventBus.post(BarcodeEvent.ReadStaffFailed)
                        })
                BarcodeKind.SALE -> eventBus.post(BarcodeEvent.ReadReceiptFailed)
                BarcodeKind.UNKNOWN -> {
                }
            }
        }
    }

    private fun onStaffReadSuccess(staff: Staff?) {
        config.currentStaff = staff
        updateViews()
    }

    private fun onItemReadSuccess(item: Item) {
        data += item
        eventBus.post(BarcodeEvent.ReadItemSuccess.apply {
            value = item
        })
        updateViews()
    }

    private fun updateViews() {
        currentPrice.postValue("${currentTotal()} リバー")
        accountButtonEnabled.postValue(data.isNotEmpty())
        listener?.onDataChanged(data)

        currentStaffVisibility.value = if (config.currentStaff == null) View.INVISIBLE else View.VISIBLE
        currentStaff.postValue("たんとう: ${config.currentStaff?.name ?: ""}")
    }

    private fun checkReachable(serverIp: String, serverPort: Int) = GlobalScope.async {
        try {
            val sock = Socket()
            sock.connect(InetSocketAddress(serverIp, serverPort), 2000)
            sock.close()
            return@async true
        } catch (e: Exception) {
            Logger.e(e, "CheckReachableTask")
            return@async false
        }
    }

    interface Listener {
        fun onDataChanged(data: List<Item>)

        fun onReadItemSuccess()

        fun onSendSaleSuccess()

        fun onAppModeChange()

        fun onStartAccount()

        fun onChangeTitleSuffix(title: String)

        fun onNotReachableServer()

        fun onShouldShowMessage(message: String)

        fun onShouldShowMessage(@StringRes messageId: Int)
    }
}
