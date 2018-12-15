package info.nukoneko.cuc.android.kidspos.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {
    private val totalPrice = MutableLiveData<Int>()
    fun getTotalPrice(): LiveData<Int> = totalPrice

    private val currentStore = MutableLiveData<Store>()
    fun getCurrentStore(): LiveData<Store> = currentStore
    private val currentStaff = MutableLiveData<Staff>()
    fun getCurrentStaff(): LiveData<Staff> = currentStaff
    var listener: Listener? = null

    private val data = MutableLiveData<List<Item>>()
    fun getData(): LiveData<List<Item>> = data

    private val app: KidsPOSApplication? = KidsPOSApplication[app]

    fun onClickClear(@Suppress("UNUSED_PARAMETER") view: View?) {
        data.postValue(emptyList())
        updateValues()
    }

    fun onClickAccount(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onStartAccount()
    }

    fun onResume() {
        val app = app ?: return

        currentStore.postValue(app.storeManager.lastStore)
        currentStaff.postValue(app.storeManager.lastStaff)

        requestUpdateTitle()

        if (!app.isPracticeModeEnabled) {
            GlobalScope.launch {
                if (!checkReachable(app.serverIp, app.serverPort).await()) {
                    listener?.onNotReachableServer()
                }
            }
        }
    }

    private fun requestUpdateTitle() {
        val app = app ?: return

        var actionBarTitle = app.getString(R.string.app_name)

        app.storeManager.lastStore?.name?.also { actionBarTitle += " [$it]" }

        if (app.isPracticeModeEnabled) actionBarTitle += " [練習モード]"

        @Suppress("ConstantConditionIf")
        if (ProjectSettings.TEST_MODE) actionBarTitle += " [テストモード]"

        listener?.onChangeTitle(actionBarTitle)
    }

    fun onBarcodeInput(barcode: String, prefix: BarcodeKind) {
        val app = app ?: return

        @Suppress("ConstantConditionIf")
        if (ProjectSettings.TEST_MODE) {
            when (prefix) {
                BarcodeKind.ITEM -> {
                    addItem(Item.create(barcode))
                }
                BarcodeKind.STAFF -> currentStaff.postValue(Staff.create(barcode))
                else -> {
                    addItem(Item.create(barcode))
                    currentStaff.postValue(Staff.create(barcode))
                }
            }
        } else {
            // サーバから取得する
            when (prefix) {
                BarcodeKind.ITEM -> app.itemManager.getItem(barcode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(app.getDefaultSubscribeScheduler())
                        .subscribe({ item ->
                            addItem(item)
                            app.postEvent(BarcodeEvent.ReadItemSuccess)
                        }, {
                            app.postEvent(BarcodeEvent.ReadItemFailed)
                        })
                BarcodeKind.STAFF -> app.staffManager.getStaff(barcode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(app.getDefaultSubscribeScheduler())
                        .subscribe({ staff ->
                            currentStaff.postValue(staff)
                            app.storeManager.saveLatestStaff(staff)
                            app.postEvent(BarcodeEvent.ReadStaffSuccess)
                        }, {
                            app.postEvent(BarcodeEvent.ReadStaffFailed)
                        })
                BarcodeKind.SALE -> app.postEvent(BarcodeEvent.ReadReceiptFailed)
                BarcodeKind.UNKNOWN -> {
                }
            }
        }

        updateValues()
    }

    private fun updateValues() {
        val total = data.value?.let {
            var sum = 0
            for (item in it) {
                sum += item.price
            }
            sum
        } ?: 0
        totalPrice.postValue(total)
    }

    private fun addItem(item: Item) {
        var items = data.value ?: emptyList()
        items += item
        KidsPOSApplication[getApplication()]?.let {
            it.postEvent(BarcodeEvent.ReadItemSuccess.apply {
                value = item
            })
        }
        data.postValue(items)

        updateValues()
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
        fun onStartAccount()

        fun onChangeTitle(title: String)

        fun onNotReachableServer()
    }
}
